package com.sgu.student_admission_system.service;

import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
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
}
