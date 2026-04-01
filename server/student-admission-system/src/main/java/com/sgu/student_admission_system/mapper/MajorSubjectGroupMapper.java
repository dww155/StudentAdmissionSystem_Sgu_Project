package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupCreationRequest;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupUpdateRequest;
import com.sgu.student_admission_system.entity.MajorSubjectGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MajorSubjectGroupMapper {

    @Mapping(target = "id", ignore = true)
    MajorSubjectGroup toMajorSubjectGroup(MajorSubjectGroupCreationRequest request);

    MajorSubjectGroupResponse toMajorSubjectGroupResponse(MajorSubjectGroup majorSubjectGroup);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "majorCode", ignore = true)
    void updateMajorSubjectGroup(@MappingTarget MajorSubjectGroup majorSubjectGroup, MajorSubjectGroupUpdateRequest request);
}
