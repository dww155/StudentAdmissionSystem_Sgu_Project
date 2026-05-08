package com.sgu.student_admission_system.mapper;

import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationCreationRequest;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationResponse;
import com.sgu.student_admission_system.dto.EnglishCertification.EnglishCertificationUpdateRequest;
import com.sgu.student_admission_system.entity.EnglishCertification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface EnglishCertificationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    EnglishCertification toEnglishCertification(EnglishCertificationCreationRequest request);

    @Mapping(target = "cccd", source = "applicant.cccd")
    EnglishCertificationResponse toEnglishCertificationResponse(EnglishCertification englishCertification);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "applicant", ignore = true)
    void updateEnglishCertification(
            @MappingTarget EnglishCertification englishCertification,
            EnglishCertificationUpdateRequest request
    );
}
