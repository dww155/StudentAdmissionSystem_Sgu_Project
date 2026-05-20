package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointCreationRequest;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointResponse;
import com.sgu.student_admission_system.dto.PriorityBonusPoint.PriorityBonusPointUpdateRequest;
import com.sgu.student_admission_system.entity.PriorityBonusPoint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PriorityBonusPointMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    PriorityBonusPoint toPriorityBonusPoint(PriorityBonusPointCreationRequest request);

    @Mapping(target = "cccd", source = "applicant.cccd")
    PriorityBonusPointResponse toPriorityBonusPointResponse(PriorityBonusPoint priorityBonusPoint);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    void updatePriorityBonusPoint(
            @MappingTarget PriorityBonusPoint priorityBonusPoint,
            PriorityBonusPointUpdateRequest request
    );
}
