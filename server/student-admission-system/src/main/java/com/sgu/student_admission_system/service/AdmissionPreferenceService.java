package com.sgu.student_admission_system.service;

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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdmissionPreferenceService {

    AdmissionPreferenceRepository admissionPreferenceRepository;
    AdmissionPreferenceMapper admissionPreferenceMapper;
    ApplicantRepository applicantRepository;
    MajorRepository majorRepository;

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

        List<Applicant> applicants = applicantRepository.findAll();
        Map<String, Applicant> applicantMap = applicants.stream().collect(Collectors.toMap(
                Applicant::getCccd,
                applicant -> applicant
        ));

        List<Major> majors = majorRepository.findAll();
        Map<String, Major> majorMap = majors.stream().collect(Collectors.toMap(
                Major::getMajorCode,
                major -> major
        ));


        List<AdmissionPreference> admissionPreferences = admissionPreferenceCreationRequests
                .stream()
                .map(admissionPreferenceCreationRequest -> {
                    Applicant applicant = applicantMap.get(admissionPreferenceCreationRequest.getCccd());
                    Major major = majorMap.get(admissionPreferenceCreationRequest.getMajorCode());

                    validateDuplicatePriority(
                            admissionPreferenceCreationRequest.getCccd(),
                            admissionPreferenceCreationRequest.getPriorityOrder(),
                            null
                    );

                    AdmissionPreference admissionPreference =
                            admissionPreferenceMapper.toAdmissionPreference(admissionPreferenceCreationRequest);
                    admissionPreference.setApplicant(applicant);
                    admissionPreference.setMajor(major);

                    return admissionPreference;
                })
                .toList();

        List<AdmissionPreference> savedAdmissionPreferences = admissionPreferenceRepository.saveAll(admissionPreferences);

        return savedAdmissionPreferences
                .stream()
                .map(admissionPreferenceMapper::toAdmissionPreferenceResponse)
                .toList();
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
}
