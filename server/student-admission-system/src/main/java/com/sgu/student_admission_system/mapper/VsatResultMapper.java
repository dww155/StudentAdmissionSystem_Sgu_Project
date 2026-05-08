package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.VsatResult.VsatResultCreationRequest;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultResponse;
import com.sgu.student_admission_system.dto.VsatResult.VsatResultUpdateRequest;
import com.sgu.student_admission_system.entity.VsatResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VsatResultMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    VsatResult toVsatResult(VsatResultCreationRequest request);

    @Mapping(target = "cccd", source = "applicant.cccd")
    VsatResultResponse toVsatResultResponse(VsatResult vsatResult);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    void updateVsatResult(@MappingTarget VsatResult vsatResult, VsatResultUpdateRequest request);
}
