package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.Major.MajorCreationRequest;
import com.sgu.student_admission_system.dto.Major.MajorResponse;
import com.sgu.student_admission_system.dto.Major.MajorUpdateRequest;
import com.sgu.student_admission_system.entity.Major;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface MajorMapper {

    @Mapping(target = "id", ignore = true)
    Major toMajor(MajorCreationRequest request);

    MajorResponse toMajorResponse(Major major);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "majorCode", ignore = true)
    void updateMajor(@MappingTarget Major major, MajorUpdateRequest request);
}
