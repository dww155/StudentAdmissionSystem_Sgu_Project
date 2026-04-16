package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.constant.Batch;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.ListMajorSubjectGroupCreationRequest;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupUpdateRequest;
import com.sgu.student_admission_system.entity.Major;
import com.sgu.student_admission_system.entity.MajorSubjectGroup;
import com.sgu.student_admission_system.entity.SubjectCombination;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.MajorSubjectGroupMapper;
import com.sgu.student_admission_system.repository.MajorRepository;
import com.sgu.student_admission_system.repository.MajorSubjectGroupRepository;
import com.sgu.student_admission_system.repository.SubjectCombinationRepository;
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
public class MajorSubjectGroupService {
    static final int JPA_BATCH_SIZE = Batch.JPA_BATCH_SIZE;

    MajorSubjectGroupRepository majorSubjectGroupRepository;
    MajorSubjectGroupMapper majorSubjectGroupMapper;
    MajorRepository majorRepository;
    SubjectCombinationRepository subjectCombinationRepository;
    EntityManager entityManager;

    @Transactional
    public MajorSubjectGroupResponse createMajorSubjectGroup(MajorSubjectGroupCreationRequest request) {
        Major major = getMajorByCode(request.getMajorCode());
        SubjectCombination subjectCombination = getSubjectCombinationByCode(request.getSubjectCombinationCode());

        validateDuplicateMajorSubjectGroup(request.getMajorCode(), request.getSubjectCombinationCode(), null);

        MajorSubjectGroup majorSubjectGroup = majorSubjectGroupMapper.toMajorSubjectGroup(request);
        majorSubjectGroup.setMajor(major);
        majorSubjectGroup.setSubjectCombination(subjectCombination);

        return majorSubjectGroupMapper.toMajorSubjectGroupResponse(
                majorSubjectGroupRepository.save(majorSubjectGroup)
        );
    }

    @Transactional
    public List<MajorSubjectGroupResponse> createMajorSubjectGroups(ListMajorSubjectGroupCreationRequest request) {
        List<MajorSubjectGroupCreationRequest> majorSubjectGroupCreationRequests =
                request.getMajorSubjectGroupCreationRequestList();

        if (majorSubjectGroupCreationRequests.isEmpty()) {
            return List.of();
        }

        Map<String, Major> majorMap = getMajorMap(majorSubjectGroupCreationRequests);
        Map<String, SubjectCombination> subjectCombinationMap =
                getSubjectCombinationMap(majorSubjectGroupCreationRequests);
        validateDuplicateMajorSubjectGroupsForBulkCreate(majorSubjectGroupCreationRequests);

        List<MajorSubjectGroupResponse> majorSubjectGroupResponses =
                new ArrayList<>(majorSubjectGroupCreationRequests.size());

        for (int i = 0; i < majorSubjectGroupCreationRequests.size(); i++) {
            MajorSubjectGroupCreationRequest creationRequest = majorSubjectGroupCreationRequests.get(i);

            MajorSubjectGroup majorSubjectGroup = majorSubjectGroupMapper.toMajorSubjectGroup(creationRequest);
            majorSubjectGroup.setMajor(getMajorFromMap(majorMap, creationRequest.getMajorCode()));
            majorSubjectGroup.setSubjectCombination(
                    getSubjectCombinationFromMap(subjectCombinationMap, creationRequest.getSubjectCombinationCode())
            );

            MajorSubjectGroup savedMajorSubjectGroup = majorSubjectGroupRepository.save(majorSubjectGroup);
            majorSubjectGroupResponses.add(majorSubjectGroupMapper.toMajorSubjectGroupResponse(savedMajorSubjectGroup));

            if ((i + 1) % JPA_BATCH_SIZE == 0) {
                flushAndClear();
            }
        }

        if (majorSubjectGroupCreationRequests.size() % JPA_BATCH_SIZE != 0) {
            flushAndClear();
        }

        return majorSubjectGroupResponses;
    }

    public MajorSubjectGroupResponse getMajorSubjectGroup(Integer id) {
        MajorSubjectGroup majorSubjectGroup = majorSubjectGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_NOT_FOUND));

        return majorSubjectGroupMapper.toMajorSubjectGroupResponse(majorSubjectGroup);
    }

    public List<MajorSubjectGroupResponse> getAllMajorSubjectGroups() {
        return majorSubjectGroupRepository.findAll()
                .stream()
                .map(majorSubjectGroupMapper::toMajorSubjectGroupResponse)
                .toList();
    }

    @Transactional
    public MajorSubjectGroupResponse updateMajorSubjectGroup(Integer id, MajorSubjectGroupUpdateRequest request) {
        MajorSubjectGroup majorSubjectGroup = majorSubjectGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_NOT_FOUND));

        SubjectCombination subjectCombination = getSubjectCombinationByCode(request.getSubjectCombinationCode());

        validateDuplicateMajorSubjectGroup(majorSubjectGroup.getMajor().getMajorCode(), request.getSubjectCombinationCode(), id);

        majorSubjectGroupMapper.updateMajorSubjectGroup(majorSubjectGroup, request);
        majorSubjectGroup.setSubjectCombination(subjectCombination);

        return majorSubjectGroupMapper.toMajorSubjectGroupResponse(
                majorSubjectGroupRepository.save(majorSubjectGroup)
        );
    }

    @Transactional
    public void deleteMajorSubjectGroup(Integer id) {
        MajorSubjectGroup majorSubjectGroup = majorSubjectGroupRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_NOT_FOUND));

        majorSubjectGroupRepository.delete(majorSubjectGroup);
    }

    private void validateDuplicateMajorSubjectGroup(String majorCode, String subjectCombinationCode, Integer id) {
        boolean duplicated = id == null
                ? majorSubjectGroupRepository.existsByMajor_MajorCodeAndSubjectCombination_Code(majorCode, subjectCombinationCode)
                : majorSubjectGroupRepository.existsByMajor_MajorCodeAndSubjectCombination_CodeAndIdNot(majorCode, subjectCombinationCode, id);

        if (duplicated) {
            throw new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_ALREADY_EXISTS);
        }
    }

    private Major getMajorByCode(String majorCode) {
        return majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private SubjectCombination getSubjectCombinationByCode(String code) {
        return subjectCombinationRepository.findByCode(code)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));
    }

    private Map<String, Major> getMajorMap(List<MajorSubjectGroupCreationRequest> majorSubjectGroupCreationRequests) {
        Set<String> majorCodes = majorSubjectGroupCreationRequests.stream()
                .map(MajorSubjectGroupCreationRequest::getMajorCode)
                .collect(Collectors.toSet());

        return majorRepository.findAllByMajorCodeIn(majorCodes)
                .stream()
                .collect(Collectors.toMap(Major::getMajorCode, Function.identity()));
    }

    private Map<String, SubjectCombination> getSubjectCombinationMap(
            List<MajorSubjectGroupCreationRequest> majorSubjectGroupCreationRequests
    ) {
        Set<String> subjectCombinationCodes = majorSubjectGroupCreationRequests.stream()
                .map(MajorSubjectGroupCreationRequest::getSubjectCombinationCode)
                .collect(Collectors.toSet());

        return subjectCombinationRepository.findAllByCodeIn(subjectCombinationCodes)
                .stream()
                .collect(Collectors.toMap(SubjectCombination::getCode, Function.identity()));
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

    private void validateDuplicateMajorSubjectGroupsForBulkCreate(
            List<MajorSubjectGroupCreationRequest> majorSubjectGroupCreationRequests
    ) {
        Set<String> requestPairKeys = new HashSet<>();
        Set<String> majorCodes = new HashSet<>();
        Set<String> subjectCombinationCodes = new HashSet<>();

        for (MajorSubjectGroupCreationRequest creationRequest : majorSubjectGroupCreationRequests) {
            majorCodes.add(creationRequest.getMajorCode());
            subjectCombinationCodes.add(creationRequest.getSubjectCombinationCode());

            String pairKey = buildMajorSubjectGroupKey(
                    creationRequest.getMajorCode(),
                    creationRequest.getSubjectCombinationCode()
            );
            if (!requestPairKeys.add(pairKey)) {
                throw new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_ALREADY_EXISTS);
            }
        }

        Set<String> existingPairKeys = majorSubjectGroupRepository
                .findAllByMajor_MajorCodeInAndSubjectCombination_CodeIn(majorCodes, subjectCombinationCodes)
                .stream()
                .map(majorSubjectGroup -> buildMajorSubjectGroupKey(
                        majorSubjectGroup.getMajor().getMajorCode(),
                        majorSubjectGroup.getSubjectCombination().getCode()
                ))
                .collect(Collectors.toSet());

        for (MajorSubjectGroupCreationRequest creationRequest : majorSubjectGroupCreationRequests) {
            String pairKey = buildMajorSubjectGroupKey(
                    creationRequest.getMajorCode(),
                    creationRequest.getSubjectCombinationCode()
            );
            if (existingPairKeys.contains(pairKey)) {
                throw new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_ALREADY_EXISTS);
            }
        }
    }

    private String buildMajorSubjectGroupKey(String majorCode, String subjectCombinationCode) {
        return majorCode + "#" + subjectCombinationCode;
    }

    private void flushAndClear() {
        majorSubjectGroupRepository.flush();
        entityManager.clear();
    }
}
