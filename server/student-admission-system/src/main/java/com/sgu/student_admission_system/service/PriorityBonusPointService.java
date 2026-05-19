package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.ListPriorityBonusPointCreationRequest;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointCreationRequest;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointResponse;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointUpdateRequest;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.PriorityBonusPoint;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.PriorityBonusPointMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.PriorityBonusPointRepository;
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
public class PriorityBonusPointService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    PriorityBonusPointRepository priorityBonusPointRepository;
    PriorityBonusPointMapper priorityBonusPointMapper;
    ApplicantRepository applicantRepository;
    EntityManager entityManager;

    @Transactional
    public PriorityBonusPointResponse createPriorityBonusPoint(PriorityBonusPointCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        validateDuplicatePriorityBonusPoint(applicant.getCccd());

        PriorityBonusPoint priorityBonusPoint = priorityBonusPointMapper.toPriorityBonusPoint(request);
        priorityBonusPoint.setApplicant(applicant);

        return priorityBonusPointMapper.toPriorityBonusPointResponse(
                priorityBonusPointRepository.save(priorityBonusPoint)
        );
    }

    @Transactional
    public List<PriorityBonusPointResponse> createPriorityBonusPoints(
            ListPriorityBonusPointCreationRequest request
    ) {
        List<PriorityBonusPointCreationRequest> priorityBonusPointCreationRequests =
                request.getPriorityBonusPointCreationRequestList();

        if (priorityBonusPointCreationRequests.isEmpty()) {
            return List.of();
        }

        validateDuplicatePriorityBonusPointsForBulkCreate(priorityBonusPointCreationRequests);
        Map<String, Applicant> applicantMap = getApplicantMap(priorityBonusPointCreationRequests);

        List<PriorityBonusPointResponse> priorityBonusPointResponses =
                new ArrayList<>(priorityBonusPointCreationRequests.size());

        for (int i = 0; i < priorityBonusPointCreationRequests.size(); i++) {
            PriorityBonusPointCreationRequest creationRequest = priorityBonusPointCreationRequests.get(i);

            PriorityBonusPoint priorityBonusPoint = priorityBonusPointMapper.toPriorityBonusPoint(creationRequest);
            priorityBonusPoint.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));

            PriorityBonusPoint savedPriorityBonusPoint = priorityBonusPointRepository.save(priorityBonusPoint);
            priorityBonusPointResponses.add(
                    priorityBonusPointMapper.toPriorityBonusPointResponse(savedPriorityBonusPoint)
            );

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (priorityBonusPointCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return priorityBonusPointResponses;
    }

    public PriorityBonusPointResponse getPriorityBonusPoint(Integer id) {
        PriorityBonusPoint priorityBonusPoint = priorityBonusPointRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_BONUS_POINT_NOT_FOUND));

        return priorityBonusPointMapper.toPriorityBonusPointResponse(priorityBonusPoint);
    }

    public PriorityBonusPointResponse getPriorityBonusPointByCccd(String cccd) {
        PriorityBonusPoint priorityBonusPoint = priorityBonusPointRepository.findByApplicant_Cccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_BONUS_POINT_NOT_FOUND));

        return priorityBonusPointMapper.toPriorityBonusPointResponse(priorityBonusPoint);
    }

    public List<PriorityBonusPointResponse> getAllPriorityBonusPoints() {
        return priorityBonusPointRepository.findAll()
                .stream()
                .map(priorityBonusPointMapper::toPriorityBonusPointResponse)
                .toList();
    }

    public Page<PriorityBonusPointResponse> getPriorityBonusPointsPaginated(Pageable pageable) {
        return priorityBonusPointRepository.findAll(pageable)
                .map(priorityBonusPointMapper::toPriorityBonusPointResponse);
    }

    @Transactional
    public PriorityBonusPointResponse updatePriorityBonusPoint(Integer id, PriorityBonusPointUpdateRequest request) {
        PriorityBonusPoint priorityBonusPoint = priorityBonusPointRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_BONUS_POINT_NOT_FOUND));

        priorityBonusPointMapper.updatePriorityBonusPoint(priorityBonusPoint, request);

        return priorityBonusPointMapper.toPriorityBonusPointResponse(
                priorityBonusPointRepository.save(priorityBonusPoint)
        );
    }

    @Transactional
    public void deletePriorityBonusPoint(Integer id) {
        PriorityBonusPoint priorityBonusPoint = priorityBonusPointRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PRIORITY_BONUS_POINT_NOT_FOUND));

        priorityBonusPointRepository.delete(priorityBonusPoint);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Map<String, Applicant> getApplicantMap(
            List<PriorityBonusPointCreationRequest> priorityBonusPointCreationRequests
    ) {
        Set<String> cccds = priorityBonusPointCreationRequests
                .stream()
                .map(PriorityBonusPointCreationRequest::getCccd)
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

    private void validateDuplicatePriorityBonusPoint(String cccd) {
        if (priorityBonusPointRepository.existsByApplicant_Cccd(cccd)) {
            throw new AppException(ErrorCode.PRIORITY_BONUS_POINT_ALREADY_EXISTS);
        }
    }

    private void validateDuplicatePriorityBonusPointsForBulkCreate(
            List<PriorityBonusPointCreationRequest> priorityBonusPointCreationRequests
    ) {
        Set<String> requestCccds = new HashSet<>();

        for (PriorityBonusPointCreationRequest creationRequest : priorityBonusPointCreationRequests) {
            if (!requestCccds.add(creationRequest.getCccd())) {
                throw new AppException(ErrorCode.PRIORITY_BONUS_POINT_ALREADY_EXISTS);
            }
        }

        Set<String> existingCccds = priorityBonusPointRepository.findAllByApplicant_CccdIn(requestCccds)
                .stream()
                .map(priorityBonusPoint -> priorityBonusPoint.getApplicant().getCccd())
                .collect(Collectors.toSet());

        if (!existingCccds.isEmpty()) {
            throw new AppException(ErrorCode.PRIORITY_BONUS_POINT_ALREADY_EXISTS);
        }
    }

    private void flushAndClear() {
        priorityBonusPointRepository.flush();
        entityManager.clear();
    }
}
