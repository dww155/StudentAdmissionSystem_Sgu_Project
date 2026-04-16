package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.Major.MajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.ListMajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorResponse;
import com.sgu.student_admission_system.dto.Major.MajorUpdateRequest;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.entity.SubjectCombination;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.MajorMapper;
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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MajorService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    MajorRepository majorRepository;
    MajorMapper majorMapper;

    SubjectCombinationRepository subjectCombinationRepository;
    EntityManager entityManager;

    @Transactional
    public MajorResponse createMajor(MajorCreationRequest request) {
        Major major = majorMapper.toMajor(request);

        SubjectCombination subjectCombination = subjectCombinationRepository.findByCode(request.getBaseCombination())
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

        major.setBaseCombination(subjectCombination);

        return majorMapper.toMajorResponse(
                majorRepository.save(major)
        );
    }

    @Transactional
    public List<MajorResponse> createMajors(ListMajorCreationRequest request) {
        List<MajorCreationRequest> majorCreationRequests = request.getMajorCreationRequestList();

        if (majorCreationRequests.isEmpty()) {
            return List.of();
        }

        Map<String, SubjectCombination> subjectCombinationMap = getSubjectCombinationMap(majorCreationRequests);
        List<MajorResponse> majorResponses = new ArrayList<>(majorCreationRequests.size());

        for (int i = 0; i < majorCreationRequests.size(); i++) {
            MajorCreationRequest creationRequest = majorCreationRequests.get(i);

            Major major = majorMapper.toMajor(creationRequest);
            major.setBaseCombination(
                    getSubjectCombinationFromMap(subjectCombinationMap, creationRequest.getBaseCombination())
            );

            Major savedMajor = majorRepository.save(major);
            majorResponses.add(majorMapper.toMajorResponse(savedMajor));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (majorCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return majorResponses;
    }

    public MajorResponse getMajor(Integer id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        return majorMapper.toMajorResponse(major);
    }

    public List<MajorResponse> getAllMajors() {
        return majorRepository.findAll()
                .stream()
                .map(majorMapper::toMajorResponse)
                .toList();
    }

    @Transactional
    public MajorResponse updateMajor(Integer id, MajorUpdateRequest request) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        majorMapper.updateMajor(major, request);

        if (request.getBaseCombination() != null) {
            SubjectCombination subjectCombination = subjectCombinationRepository
                    .findByCode(request.getBaseCombination())
                    .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));

            major.setBaseCombination(subjectCombination);
        }
        return majorMapper.toMajorResponse(
                majorRepository.save(major)
        );
    }

    @Transactional
    public void deleteMajor(Integer id) {
        Major major = majorRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));

        majorRepository.delete(major);
    }

    private Map<String, SubjectCombination> getSubjectCombinationMap(List<MajorCreationRequest> majorCreationRequests) {
        Set<String> baseCombinations = majorCreationRequests.stream()
                .map(MajorCreationRequest::getBaseCombination)
                .collect(Collectors.toSet());

        return subjectCombinationRepository.findAllByCodeIn(baseCombinations)
                .stream()
                .collect(Collectors.toMap(SubjectCombination::getCode, Function.identity()));
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
        majorRepository.flush();
        entityManager.clear();
    }
}
