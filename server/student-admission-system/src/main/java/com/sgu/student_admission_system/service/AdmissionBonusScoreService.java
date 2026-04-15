package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreUpdateRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.ListAdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.entity.AdmissionBonusScore;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.entity.SubjectCombination;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.AdmissionBonusScoreMapper;
import com.sgu.student_admission_system.repository.AdmissionBonusScoreRepository;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.MajorRepository;
import com.sgu.student_admission_system.repository.SubjectCombinationRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdmissionBonusScoreService {
    static final int BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    AdmissionBonusScoreRepository admissionBonusScoreRepository;
    AdmissionBonusScoreMapper admissionBonusScoreMapper;
    ApplicantRepository applicantRepository;
    MajorRepository majorRepository;
    SubjectCombinationRepository subjectCombinationRepository;
    EntityManager entityManager;

    @Transactional
    public AdmissionBonusScoreResponse createAdmissionBonusScore(AdmissionBonusScoreCreationRequest request) {

        Applicant applicant = getApplicantByCccd(request.getCccd());
        Major major = getMajorByCode(request.getMajorCode());
        SubjectCombination subjectCombination = getSubjectCombinationByCode(request.getSubjectCombinationCode());

        AdmissionBonusScore admissionBonusScore = admissionBonusScoreMapper.toAdmissionBonusScore(request);
        admissionBonusScore.setApplicant(applicant);
        admissionBonusScore.setMajor(major);
        admissionBonusScore.setSubjectCombination(subjectCombination);

        return admissionBonusScoreMapper.toAdmissionBonusScoreResponse(
                admissionBonusScoreRepository.save(admissionBonusScore)
        );
    }

    @Transactional
    public List<AdmissionBonusScoreResponse> createAdmissionBonusScores(ListAdmissionBonusScoreCreationRequest request) {
        List<AdmissionBonusScoreCreationRequest> admissionBonusScoreCreationRequests =
                request.getAdmissionBonusScoreCreationRequestList();

        if (admissionBonusScoreCreationRequests.isEmpty()) {
            return List.of();
        }

        Map<String, Applicant> applicantMap = getApplicantMap(admissionBonusScoreCreationRequests);
        Map<String, Major> majorMap = getMajorMap(admissionBonusScoreCreationRequests);
        Map<String, SubjectCombination> subjectCombinationMap = getSubjectCombinationMap(admissionBonusScoreCreationRequests);

        List<AdmissionBonusScoreResponse> admissionBonusScoreResponses =
                new ArrayList<>(admissionBonusScoreCreationRequests.size());

        for (int i = 0; i < admissionBonusScoreCreationRequests.size(); i++) {
            AdmissionBonusScoreCreationRequest creationRequest = admissionBonusScoreCreationRequests.get(i);

            AdmissionBonusScore admissionBonusScore = admissionBonusScoreMapper.toAdmissionBonusScore(creationRequest);
            admissionBonusScore.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));
            admissionBonusScore.setMajor(getMajorFromMap(majorMap, creationRequest.getMajorCode()));
            admissionBonusScore.setSubjectCombination(
                    getSubjectCombinationFromMap(subjectCombinationMap, creationRequest.getSubjectCombinationCode())
            );

            AdmissionBonusScore savedAdmissionBonusScore = admissionBonusScoreRepository.save(admissionBonusScore);
            admissionBonusScoreResponses.add(
                    admissionBonusScoreMapper.toAdmissionBonusScoreResponse(savedAdmissionBonusScore)
            );

            if ((i + 1) % BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (admissionBonusScoreCreationRequests.size() % BATCH_SIZE != 0) {
            flushAndClear();
        }

        return admissionBonusScoreResponses;
    }

    public AdmissionBonusScoreResponse getAdmissionBonusScore(Integer id) {
        AdmissionBonusScore admissionBonusScore = admissionBonusScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_BONUS_SCORE_NOT_FOUND));

        return admissionBonusScoreMapper.toAdmissionBonusScoreResponse(admissionBonusScore);
    }

    public List<AdmissionBonusScoreResponse> getAllAdmissionBonusScores() {
        return admissionBonusScoreRepository.findAll()
                .stream()
                .map(admissionBonusScoreMapper::toAdmissionBonusScoreResponse)
                .toList();
    }

    @Transactional
    public AdmissionBonusScoreResponse updateAdmissionBonusScore(Integer id, AdmissionBonusScoreUpdateRequest request) {
        AdmissionBonusScore admissionBonusScore = admissionBonusScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_BONUS_SCORE_NOT_FOUND));

        Major major = getMajorByCode(request.getMajorCode());
        SubjectCombination subjectCombination = getSubjectCombinationByCode(request.getSubjectCombinationCode());

        admissionBonusScoreMapper.updateAdmissionBonusScore(admissionBonusScore, request);
        admissionBonusScore.setMajor(major);
        admissionBonusScore.setSubjectCombination(subjectCombination);

        return admissionBonusScoreMapper.toAdmissionBonusScoreResponse(
                admissionBonusScoreRepository.save(admissionBonusScore)
        );
    }

    @Transactional
    public void deleteAdmissionBonusScore(Integer id) {
        AdmissionBonusScore admissionBonusScore = admissionBonusScoreRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ADMISSION_BONUS_SCORE_NOT_FOUND));

        admissionBonusScoreRepository.delete(admissionBonusScore);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Major getMajorByCode(String majorCode) {
        return majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private SubjectCombination getSubjectCombinationByCode(String subjectCombinationCode) {
        return subjectCombinationRepository.findByCode(subjectCombinationCode)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));
    }

    private Map<String, Applicant> getApplicantMap(
            List<AdmissionBonusScoreCreationRequest> admissionBonusScoreCreationRequests
    ) {
        List<String> cccds = admissionBonusScoreCreationRequests
                .stream()
                .map(AdmissionBonusScoreCreationRequest::getCccd)
                .toList();

        return applicantRepository.findAllByCccd(cccds)
                .stream()
                .collect(Collectors.toMap(Applicant::getCccd, Function.identity()));
    }

    private Map<String, Major> getMajorMap(List<AdmissionBonusScoreCreationRequest> admissionBonusScoreCreationRequests) {
        List<String> majorCodes = admissionBonusScoreCreationRequests
                .stream()
                .map(AdmissionBonusScoreCreationRequest::getMajorCode)
                .toList();

        return majorRepository.findAllByMajorCode(majorCodes)
                .stream()
                .collect(Collectors.toMap(Major::getMajorCode, Function.identity()));
    }

    private Map<String, SubjectCombination> getSubjectCombinationMap(
            List<AdmissionBonusScoreCreationRequest> admissionBonusScoreCreationRequests
    ) {
        List<String> subjectCombinationCodes = admissionBonusScoreCreationRequests
                .stream()
                .map(AdmissionBonusScoreCreationRequest::getSubjectCombinationCode)
                .toList();

        return subjectCombinationRepository.findAllByCode(subjectCombinationCodes)
                .stream()
                .collect(Collectors.toMap(SubjectCombination::getCode, Function.identity()));
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

    private SubjectCombination getSubjectCombinationFromMap(
            Map<String, SubjectCombination> subjectCombinationMap,
            String subjectCombinationCode
    ) {
        SubjectCombination subjectCombination = subjectCombinationMap.get(subjectCombinationCode);
        if (subjectCombination == null) {
            throw new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND);
        }

        return subjectCombination;
    }

    private void flushAndClear() {
        admissionBonusScoreRepository.flush();
        entityManager.clear();
    }
}
