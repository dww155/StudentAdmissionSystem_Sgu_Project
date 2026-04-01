package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupUpdateRequest;
import com.sgu.student_admission_system.entity.MajorSubjectGroup;
import com.sgu.student_admission_system.exception.AppException;
import com.sgu.student_admission_system.exception.ErrorCode;
import com.sgu.student_admission_system.mapper.MajorSubjectGroupMapper;
import com.sgu.student_admission_system.repository.MajorRepository;
import com.sgu.student_admission_system.repository.MajorSubjectGroupRepository;
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
public class MajorSubjectGroupService {

    MajorSubjectGroupRepository majorSubjectGroupRepository;
    MajorSubjectGroupMapper majorSubjectGroupMapper;
    MajorRepository majorRepository;
    SubjectCombinationRepository subjectCombinationRepository;

    @Transactional
    public MajorSubjectGroupResponse createMajorSubjectGroup(MajorSubjectGroupCreationRequest request) {
        validateMajorExists(request.getMajorCode());
        validateSubjectCombinationExists(request.getSubjectCombinationCode());

        validateDuplicateMajorSubjectGroup(request.getMajorCode(), request.getSubjectCombinationCode(), null);

        MajorSubjectGroup majorSubjectGroup = majorSubjectGroupMapper.toMajorSubjectGroup(request);

        return majorSubjectGroupMapper.toMajorSubjectGroupResponse(
                majorSubjectGroupRepository.save(majorSubjectGroup)
        );
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

        validateSubjectCombinationExists(request.getSubjectCombinationCode());

        validateDuplicateMajorSubjectGroup(majorSubjectGroup.getMajorCode(), request.getSubjectCombinationCode(), id);

        majorSubjectGroupMapper.updateMajorSubjectGroup(majorSubjectGroup, request);

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

    private void validateMajorExists(String majorCode) {
        majorRepository.findByMajorCode(majorCode)
                .orElseThrow(() -> new AppException(ErrorCode.MAJOR_NOT_FOUND));
    }

    private void validateSubjectCombinationExists(String subjectCombinationCode) {
        subjectCombinationRepository.findByCode(subjectCombinationCode)
                .orElseThrow(() -> new AppException(ErrorCode.SUBJECT_COMBINATION_NOT_FOUND));
    }

    private void validateDuplicateMajorSubjectGroup(String majorCode, String subjectCombinationCode, Integer id) {
        boolean duplicated = id == null
                ? majorSubjectGroupRepository.existsByMajorCodeAndSubjectCombinationCode(majorCode, subjectCombinationCode)
                : majorSubjectGroupRepository.existsByMajorCodeAndSubjectCombinationCodeAndIdNot(majorCode, subjectCombinationCode, id);

        if (duplicated) {
            throw new AppException(ErrorCode.MAJOR_SUBJECT_GROUP_ALREADY_EXISTS);
        }
    }
}
