package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.VsatResult.ListVsatResultCreationRequest;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultCreationRequest;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultResponse;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultUpdateRequest;
import com.sgu.student_admission_system.entity.Applicant;
import com.sgu.student_admission_system.entity.VsatResult;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.VsatResultMapper;
import com.sgu.student_admission_system.repository.ApplicantRepository;
import com.sgu.student_admission_system.repository.VsatResultRepository;
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
public class VsatResultService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    VsatResultRepository vsatResultRepository;
    VsatResultMapper vsatResultMapper;
    ApplicantRepository applicantRepository;
    EntityManager entityManager;

    @Transactional
    public VsatResultResponse createVsatResult(VsatResultCreationRequest request) {
        Applicant applicant = getApplicantByCccd(request.getCccd());
        validateDuplicateVsatResult(applicant.getCccd());

        VsatResult vsatResult = vsatResultMapper.toVsatResult(request);
        vsatResult.setApplicant(applicant);

        return vsatResultMapper.toVsatResultResponse(
                vsatResultRepository.save(vsatResult)
        );
    }

    @Transactional
    public List<VsatResultResponse> createVsatResults(ListVsatResultCreationRequest request) {
        List<VsatResultCreationRequest> vsatResultCreationRequests = request.getVsatResultCreationRequestList();

        if (vsatResultCreationRequests.isEmpty()) {
            return List.of();
        }

        validateDuplicateVsatResultsForBulkCreate(vsatResultCreationRequests);
        Map<String, Applicant> applicantMap = getApplicantMap(vsatResultCreationRequests);

        List<VsatResultResponse> vsatResultResponses = new ArrayList<>(vsatResultCreationRequests.size());

        for (int i = 0; i < vsatResultCreationRequests.size(); i++) {
            VsatResultCreationRequest creationRequest = vsatResultCreationRequests.get(i);

            VsatResult vsatResult = vsatResultMapper.toVsatResult(creationRequest);
            vsatResult.setApplicant(getApplicantFromMap(applicantMap, creationRequest.getCccd()));

            VsatResult savedVsatResult = vsatResultRepository.save(vsatResult);
            vsatResultResponses.add(vsatResultMapper.toVsatResultResponse(savedVsatResult));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (vsatResultCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return vsatResultResponses;
    }

    public VsatResultResponse getVsatResult(Long id) {
        VsatResult vsatResult = vsatResultRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VSAT_RESULT_NOT_FOUND));

        return vsatResultMapper.toVsatResultResponse(vsatResult);
    }

    public VsatResultResponse getVsatResultByCccd(String cccd) {
        VsatResult vsatResult = vsatResultRepository.findByApplicant_Cccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.VSAT_RESULT_NOT_FOUND));

        return vsatResultMapper.toVsatResultResponse(vsatResult);
    }

    public List<VsatResultResponse> getAllVsatResults() {
        return vsatResultRepository.findAll()
                .stream()
                .map(vsatResultMapper::toVsatResultResponse)
                .toList();
    }

    public Page<VsatResultResponse> getVsatResultsPaginated(Pageable pageable) {
        return vsatResultRepository.findAll(pageable)
                .map(vsatResultMapper::toVsatResultResponse);
    }

    @Transactional
    public VsatResultResponse updateVsatResult(Long id, VsatResultUpdateRequest request) {
        VsatResult vsatResult = vsatResultRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VSAT_RESULT_NOT_FOUND));

        vsatResultMapper.updateVsatResult(vsatResult, request);

        return vsatResultMapper.toVsatResultResponse(
                vsatResultRepository.save(vsatResult)
        );
    }

    @Transactional
    public void deleteVsatResult(Long id) {
        VsatResult vsatResult = vsatResultRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.VSAT_RESULT_NOT_FOUND));

        vsatResultRepository.delete(vsatResult);
    }

    private Applicant getApplicantByCccd(String cccd) {
        return applicantRepository.findByCccd(cccd)
                .orElseThrow(() -> new AppException(ErrorCode.APPLICANT_NOT_FOUND));
    }

    private Map<String, Applicant> getApplicantMap(List<VsatResultCreationRequest> vsatResultCreationRequests) {
        Set<String> cccds = vsatResultCreationRequests
                .stream()
                .map(VsatResultCreationRequest::getCccd)
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

    private void validateDuplicateVsatResult(String cccd) {
        if (vsatResultRepository.existsByApplicant_Cccd(cccd)) {
            throw new AppException(ErrorCode.VSAT_RESULT_ALREADY_EXISTS);
        }
    }

    private void validateDuplicateVsatResultsForBulkCreate(List<VsatResultCreationRequest> vsatResultCreationRequests) {
        Set<String> requestCccds = new HashSet<>();

        for (VsatResultCreationRequest creationRequest : vsatResultCreationRequests) {
            if (!requestCccds.add(creationRequest.getCccd())) {
                throw new AppException(ErrorCode.VSAT_RESULT_ALREADY_EXISTS);
            }
        }

        Set<String> existingCccds = vsatResultRepository.findAllByApplicant_CccdIn(requestCccds)
                .stream()
                .map(vsatResult -> vsatResult.getApplicant().getCccd())
                .collect(Collectors.toSet());

        if (!existingCccds.isEmpty()) {
            throw new AppException(ErrorCode.VSAT_RESULT_ALREADY_EXISTS);
        }
    }

    private void flushAndClear() {
        vsatResultRepository.flush();
        entityManager.clear();
    }
}
