package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationCreationRequest;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationResponse;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationUpdateRequest;
import com.sgu.student_admission_system.dto.EnglishCertification.ListEnglishCertificationCreationRequest;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.EnglishCertification;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.EnglishCertificationMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.EnglishCertificationRepository;
import jakarta.persistence.EntityManager;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EnglishCertificationService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    EnglishCertificationRepository englishCertificationRepository;
    EnglishCertificationMapper englishCertificationMapper;
    ApplicantRepository applicantRepository;
    EntityManager entityManager;

    @Transactional
    public EnglishCertificationResponse createEnglishCertification(EnglishCertificationCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        validateDuplicateEnglishCertification(applicant.getCccd());

        EnglishCertification englishCertification = englishCertificationMapper.toEnglishCertification(request);
        englishCertification.setApplicant(applicant);

        return englishCertificationMapper.toEnglishCertificationResponse(
                englishCertificationRepository.save(englishCertification)
        );
    }

    @Transactional
    public List<EnglishCertificationResponse> createEnglishCertifications(
            ListEnglishCertificationCreationRequest request
    ) {
        List<EnglishCertificationCreationRequest> englishCertificationCreationRequests =
                request.getEnglishCertificationCreationRequestList();

        if (englishCertificationCreationRequests.isEmpty()) {
            return List.of();
        }

        validateDuplicateEnglishCertificationsForBulkCreate(englishCertificationCreationRequests);
        Map<String, Applicant> applicantMap = getApplicantMap(englishCertificationCreationRequests);

        List<EnglishCertificationResponse> englishCertificationResponses =
                new ArrayList<>(englishCertificationCreationRequests.size());

        for (int i = 0; i < englishCertificationCreationRequests.size(); i++) {
            EnglishCertificationCreationRequest creationRequest = englishCertificationCreationRequests.get(i);

            EnglishCertification englishCertification = englishCertificationMapper.toEnglishCertification(creationRequest);
            englishCertification.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));

            EnglishCertification savedEnglishCertification = englishCertificationRepository.save(englishCertification);
            englishCertificationResponses.add(
                    englishCertificationMapper.toEnglishCertificationResponse(savedEnglishCertification)
            );

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (englishCertificationCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return englishCertificationResponses;
    }

    public EnglishCertificationResponse getEnglishCertification(Long id) {
        EnglishCertification englishCertification = englishCertificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENGLISH_CERTIFICATION_NOT_FOUND));

        return englishCertificationMapper.toEnglishCertificationResponse(englishCertification);
    }

    public EnglishCertificationResponse getEnglishCertificationByCccd(String cccd) {
        EnglishCertification englishCertification = englishCertificationRepository.findByApplicant_Cccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.ENGLISH_CERTIFICATION_NOT_FOUND));

        return englishCertificationMapper.toEnglishCertificationResponse(englishCertification);
    }

    public List<EnglishCertificationResponse> getAllEnglishCertifications() {
        return englishCertificationRepository.findAll()
                .stream()
                .map(englishCertificationMapper::toEnglishCertificationResponse)
                .toList();
    }

    public Page<EnglishCertificationResponse> getEnglishCertificationsPaginated(Pageable pageable) {
        return englishCertificationRepository.findAll(pageable)
                .map(englishCertificationMapper::toEnglishCertificationResponse);
    }

    @Transactional
    public EnglishCertificationResponse updateEnglishCertification(Long id, EnglishCertificationUpdateRequest request) {
        EnglishCertification englishCertification = englishCertificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENGLISH_CERTIFICATION_NOT_FOUND));

        englishCertificationMapper.updateEnglishCertification(englishCertification, request);

        return englishCertificationMapper.toEnglishCertificationResponse(
                englishCertificationRepository.save(englishCertification)
        );
    }

    @Transactional
    public void deleteEnglishCertification(Long id) {
        EnglishCertification englishCertification = englishCertificationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.ENGLISH_CERTIFICATION_NOT_FOUND));

        englishCertificationRepository.delete(englishCertification);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Map<String, Applicant> getApplicantMap(
            List<EnglishCertificationCreationRequest> englishCertificationCreationRequests
    ) {
        Set<String> cccds = englishCertificationCreationRequests
                .stream()
                .map(EnglishCertificationCreationRequest::getCccd)
                .collect(Collectors.toSet());

        return applicantRepository.findAllByCccdIn(cccds)
                .stream()
                .collect(Collectors.toMap(Applicant::getCccd, Function.identity()));
    }

    private Applicant getApplicantFromMap(Map<String, Applicant> applicantMap, String cccd) {
        Applicant applicant = applicantMap.get(cccd);
        if (applicant == null) {
            throw new AppException(ErrorCode.APPLICANT_NOT_FOUND);
        }

        return applicant;
    }

    private void validateDuplicateEnglishCertification(String cccd) {
        if (englishCertificationRepository.existsByApplicant_Cccd(cccd)) {
            throw new AppException(ErrorCode.ENGLISH_CERTIFICATION_ALREADY_EXISTS);
        }
    }

    private void validateDuplicateEnglishCertificationsForBulkCreate(
            List<EnglishCertificationCreationRequest> englishCertificationCreationRequests
    ) {
        Set<String> requestCccds = new HashSet<>();

        for (EnglishCertificationCreationRequest creationRequest : englishCertificationCreationRequests) {
            if (!requestCccds.add(creationRequest.getCccd())) {
                throw new AppException(ErrorCode.ENGLISH_CERTIFICATION_ALREADY_EXISTS);
            }
        }

        Set<String> existingCccds = englishCertificationRepository.findAllByApplicant_CccdIn(requestCccds)
                .stream()
                .map(englishCertification -> englishCertification.getApplicant().getCccd())
                .collect(Collectors.toSet());

        if (!existingCccds.isEmpty()) {
            throw new AppException(ErrorCode.ENGLISH_CERTIFICATION_ALREADY_EXISTS);
        }
    }

    private void flushAndClear() {
        englishCertificationRepository.flush();
        entityManager.clear();
    }
}
