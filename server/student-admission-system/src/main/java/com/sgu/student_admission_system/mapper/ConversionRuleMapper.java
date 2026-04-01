package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.student_admission_system.dto.ConversionRule.ConversionRuleUpdateRequest;
import com.sgu.student_admission_system.entity.ConversionRule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ConversionRuleMapper {

    @Mapping(target = "id", ignore = true)
    ConversionRule toConversionRule(ConversionRuleCreationRequest request);

    ConversionRuleResponse toConversionRuleResponse(ConversionRule conversionRule);

    @Mapping(target = "id", ignore = true)
    void updateConversionRule(@MappingTarget ConversionRule conversionRule, ConversionRuleUpdateRequest request);
}
