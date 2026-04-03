package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.student_admission_system.dto.AdmissionBonusScore.AdmissionBonusScoreUpdateRequest;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class AdmissionBonusScoreService {

    AdmissionBonusScoreRepository admissionBonusScoreRepository;
    AdmissionBonusScoreMapper admissionBonusScoreMapper;
    ApplicantRepository applicantRepository;
    MajorRepository majorRepository;
    SubjectCombinationRepository subjectCombinationRepository;

    @Transactional
    public AdmissionBonusScoreResponse createAdmissionBonusScore(AdmissionBonusScoreCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        Major major = getMajorByCodeOrNull(request.getMajorCode());
        SubjectCombination subjectCombination = getSubjectCombinationByCodeOrNull(request.getSubjectCombinationCode());

        AdmissionBonusScore admissionBonusScore = admissionBonusScoreMapper.toAdmissionBonusScore(request);
        admissionBonusScore.setApplicant(applicant);
        admissionBonusScore.setMajor(major);
        admissionBonusScore.setSubjectCombination(subjectCombination);

        return admissionBonusScoreMapper.toAdmissionBonusScoreResponse(
                admissionBonusScoreRepository.save(admissionBonusScore)
        );
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

        Major major = getMajorByCodeOrNull(request.getMajorCode());
        SubjectCombination subjectCombination = getSubjectCombinationByCodeOrNull(request.getSubjectCombinationCode());

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

    private Major getMajorByCodeOrNull(String majorCode) {
        if (majorCode == null || majorCode.isBlank()) {
            return null;
        }

        return majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private SubjectCombination getSubjectCombinationByCodeOrNull(String subjectCombinationCode) {
        if (subjectCombinationCode == null || subjectCombinationCode.isBlank()) {
            return null;
        }

        return subjectCombinationRepository.findByCode(subjectCombinationCode)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));
    }
}
