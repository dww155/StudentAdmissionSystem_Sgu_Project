package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.ListAdmissionPreferenceCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.student_admission_system.dto.AdmissionPreference.AdmissionPreferenceUpdateRequest;
import com.sgu.student_admission_system.entity.AdmissionPreference;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.AdmissionPreferenceMapper;
import com.sgu.student_admission_system.repository.AdmissionPreferenceRepository;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.MajorRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdmissionPreferenceService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    AdmissionPreferenceRepository admissionPreferenceRepository;
    AdmissionPreferenceMapper admissionPreferenceMapper;
    ApplicantRepository applicantRepository;
    MajorRepository majorRepository;
    EntityManager entityManager;

    @Transactional
    public AdmissionPreferenceResponse createAdmissionPreference(AdmissionPreferenceCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        Major major = getMajorByCode(request.getMajorCode());

        validateDuplicatePriority(request.getCccd(), request.getPriorityOrder(), null);

        AdmissionPreference admissionPreference = admissionPreferenceMapper.toAdmissionPreference(request);
        admissionPreference.setApplicant(applicant);
        admissionPreference.setMajor(major);

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(
                admissionPreferenceRepository.save(admissionPreference)
        );
    }

    @Transactional
    public List<AdmissionPreferenceResponse> createAdmissionPreferences(ListAdmissionPreferenceCreationRequest request) {
        List<AdmissionPreferenceCreationRequest> admissionPreferenceCreationRequests =
                request.getAdmissionPreferenceCreationRequestList();

        if (admissionPreferenceCreationRequests.isEmpty()) {
            return List.of();
        }

        Map<String, Applicant> applicantMap = getApplicantMap(admissionPreferenceCreationRequests);
        Map<String, Major> majorMap = getMajorMap(admissionPreferenceCreationRequests);
        validateDuplicatePrioritiesForBulkCreate(admissionPreferenceCreationRequests);

        List<AdmissionPreferenceResponse> admissionPreferenceResponses =
                new ArrayList<>(admissionPreferenceCreationRequests.size());

        for (int i = 0; i < admissionPreferenceCreationRequests.size(); i++) {
            AdmissionPreferenceCreationRequest creationRequest = admissionPreferenceCreationRequests.get(i);

            AdmissionPreference admissionPreference = admissionPreferenceMapper.toAdmissionPreference(creationRequest);
            admissionPreference.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));
            admissionPreference.setMajor(getMajorFromMap(majorMap, creationRequest.getMajorCode()));

            AdmissionPreference savedAdmissionPreference = admissionPreferenceRepository.save(admissionPreference);
            admissionPreferenceResponses.add(admissionPreferenceMapper.toAdmissionPreferenceResponse(savedAdmissionPreference));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (admissionPreferenceCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return admissionPreferenceResponses;
    }

    public AdmissionPreferenceResponse getAdmissionPreference(Integer id) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(admissionPreference);
    }

    public List<AdmissionPreferenceResponse> getAllAdmissionPreferences() {
        return admissionPreferenceRepository.findAll()
                .stream()
                .map(admissionPreferenceMapper::toAdmissionPreferenceResponse)
                .toList();
    }

    @Transactional
    public AdmissionPreferenceResponse updateAdmissionPreference(Integer id, AdmissionPreferenceUpdateRequest request) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        Major major = getMajorByCode(request.getMajorCode());

        validateDuplicatePriority(admissionPreference.getApplicant().getCccd(), request.getPriorityOrder(), id);

        admissionPreferenceMapper.updateAdmissionPreference(admissionPreference, request);
        admissionPreference.setMajor(major);

        return admissionPreferenceMapper.toAdmissionPreferenceResponse(
                admissionPreferenceRepository.save(admissionPreference)
        );
    }

    @Transactional
    public void deleteAdmissionPreference(Integer id) {
        AdmissionPreference admissionPreference = admissionPreferenceRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_PREFERENCE_NOT_FOUND));

        admissionPreferenceRepository.delete(admissionPreference);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Major getMajorByCode(String majorCode) {
        return majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private void validateDuplicatePriority(String cccd, Integer priorityOrder, Integer preferenceId) {
        boolean duplicated = preferenceId == null
                ? admissionPreferenceRepository.existsByApplicant_CccdAndPriorityOrder(cccd, priorityOrder)
                : admissionPreferenceRepository.existsByApplicant_CccdAndPriorityOrderAndIdNot(cccd, priorityOrder, preferenceId);

        if (duplicated) {
            throw new AppException(ErrorCode.DUPLICATE_ADMISSION_PREFERENCE_PRIORITY);
        }
    }

    private Map<String, Applicant> getApplicantMap(
            List<AdmissionPreferenceCreationRequest> admissionPreferenceCreationRequests
    ) {
        Set<String> cccds = admissionPreferenceCreationRequests.stream()
                .map(AdmissionPreferenceCreationRequest::getCccd)
                .collect(Collectors.toSet());

        return applicantRepository.findAllByCccdIn(cccds)
                .stream()
                .collect(Collectors.toMap(Applicant::getCccd, Function.identity()));
    }

    private Map<String, Major> getMajorMap(List<AdmissionPreferenceCreationRequest> admissionPreferenceCreationRequests) {
        Set<String> majorCodes = admissionPreferenceCreationRequests.stream()
                .map(AdmissionPreferenceCreationRequest::getMajorCode)
                .collect(Collectors.toSet());

        return majorRepository.findAllByMajorCodeIn(majorCodes)
                .stream()
                .collect(Collectors.toMap(Major::getMajorCode, Function.identity()));
    }

    private Applicant getApplicantFromMap(Map<String, Applicant> applicantMap, String cccd) {
        Applicant applicant = applicantMap.get(cccd);
        if (applicant == null) {
            throw new AppException(ErrorCode.APPLICANT_NOT_FOUND);
        }

        return applicant;
    }

    private Major getMajorFromMap(Map<String, Major> majorMap, String majorCode) {
        Major major = majorMap.get(majorCode);
        if (major == null) {
            throw new AppException(ErrorCode.MAJOR_NOT_FOUND);
        }

        return major;
    }

    private void validateDuplicatePrioritiesForBulkCreate(
            List<AdmissionPreferenceCreationRequest> admissionPreferenceCreationRequests
    ) {
        Set<String> requestPriorityKeys = new HashSet<>();
        Set<String> cccds = new HashSet<>();

        for (AdmissionPreferenceCreationRequest creationRequest : admissionPreferenceCreationRequests) {
            cccds.add(creationRequest.getCccd());

            String priorityKey = buildPriorityKey(creationRequest.getCccd(), creationRequest.getPriorityOrder());
            if (!requestPriorityKeys.add(priorityKey)) {
                throw new AppException(ErrorCode.DUPLICATE_ADMISSION_PREFERENCE_PRIORITY);
            }
        }

        Set<String> existingPriorityKeys = admissionPreferenceRepository.findAllByApplicant_CccdIn(cccds)
                .stream()
                .map(admissionPreference -> buildPriorityKey(
                        admissionPreference.getApplicant().getCccd(),
                        admissionPreference.getPriorityOrder()
                ))
                .collect(Collectors.toSet());

        for (AdmissionPreferenceCreationRequest creationRequest : admissionPreferenceCreationRequests) {
            String priorityKey = buildPriorityKey(creationRequest.getCccd(), creationRequest.getPriorityOrder());
            if (existingPriorityKeys.contains(priorityKey)) {
                throw new AppException(ErrorCode.DUPLICATE_ADMISSION_PREFERENCE_PRIORITY);
            }
        }
    }

    private String buildPriorityKey(String cccd, Integer priorityOrder) {
        return cccd + "#" + priorityOrder;
    }

    private void flushAndClear() {
        admissionPreferenceRepository.flush();
        entityManager.clear();
    }
}
