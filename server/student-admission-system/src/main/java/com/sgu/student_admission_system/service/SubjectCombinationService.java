package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationCreationRequest;
import com.sgu.student_admission_system.dto.SubjectCombination.ListSubjectCombinationCreationRequest;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationUpdateRequest;
import com.sgu.student_admission_system.entity.SubjectCombination;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.SubjectCombinationMapper;
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

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class SubjectCombinationService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    SubjectCombinationRepository subjectCombinationRepository;
    SubjectCombinationMapper subjectCombinationMapper;
    EntityManager entityManager;

    @Transactional
    public SubjectCombinationResponse createSubjectCombination(SubjectCombinationCreationRequest request) {
        SubjectCombination subjectCombination = subjectCombinationMapper.toSubjectCombination(request);

        return subjectCombinationMapper.toSubjectCombinationResponse(
                subjectCombinationRepository.save(subjectCombination)
        );
    }

    @Transactional
    public List<SubjectCombinationResponse> createSubjectCombinations(ListSubjectCombinationCreationRequest request) {
        List<SubjectCombinationCreationRequest> subjectCombinationCreationRequests =
                request.getSubjectCombinationCreationRequestList();

        if (subjectCombinationCreationRequests.isEmpty()) {
            return List.of();
        }

        List<SubjectCombinationResponse> subjectCombinationResponses =
                new ArrayList<>(subjectCombinationCreationRequests.size());

        for (int i = 0; i < subjectCombinationCreationRequests.size(); i++) {
            SubjectCombination subjectCombination =
                    subjectCombinationMapper.toSubjectCombination(subjectCombinationCreationRequests.get(i));
            SubjectCombination savedSubjectCombination = subjectCombinationRepository.save(subjectCombination);
            subjectCombinationResponses.add(subjectCombinationMapper.toSubjectCombinationResponse(savedSubjectCombination));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (subjectCombinationCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return subjectCombinationResponses;
    }

    public SubjectCombinationResponse getSubjectCombination(Integer id) {
        SubjectCombination subjectCombination = subjectCombinationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

        return subjectCombinationMapper.toSubjectCombinationResponse(subjectCombination);
    }

    public List<SubjectCombinationResponse> getAllSubjectCombinations() {
        return subjectCombinationRepository.findAll()
                .stream()
                .map(subjectCombinationMapper::toSubjectCombinationResponse)
                .toList();
    }

    @Transactional
    public SubjectCombinationResponse updateSubjectCombination(Integer id, SubjectCombinationUpdateRequest request) {
        SubjectCombination subjectCombination = subjectCombinationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

        subjectCombinationMapper.updateSubjectCombination(subjectCombination, request);

        return subjectCombinationMapper.toSubjectCombinationResponse(
                subjectCombinationRepository.save(subjectCombination)
        );
    }

    @Transactional
    public void deleteSubjectCombination(Integer id) {
        SubjectCombination subjectCombination = subjectCombinationRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

        subjectCombinationRepository.delete(subjectCombination);
    }

    private void flushAndClear() {
        subjectCombinationRepository.flush();
        entityManager.clear();
    }
}
