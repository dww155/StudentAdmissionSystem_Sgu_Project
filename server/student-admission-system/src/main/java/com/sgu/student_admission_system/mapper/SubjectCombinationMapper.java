package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationCreationRequest;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.student_admission_system.dto.SubjectCombination.SubjectCombinationUpdateRequest;
import com.sgu.student_admission_system.entity.SubjectCombination;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface SubjectCombinationMapper {

    @Mapping(target = "id", ignore = true)
    SubjectCombination toSubjectCombination(SubjectCombinationCreationRequest request);

    SubjectCombinationResponse toSubjectCombinationResponse(SubjectCombination subjectCombination);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateSubjectCombination(@MappingTarget SubjectCombination subjectCombination, SubjectCombinationUpdateRequest request);
}
