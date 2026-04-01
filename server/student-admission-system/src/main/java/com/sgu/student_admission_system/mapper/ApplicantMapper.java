package com.sgu.student_admission_system.mapper;


import com.sgu.student_admission_system.dto.Applicant.ApplicantCreationRequest;
import com.sgu.student_admission_system.dto.Applicant.ApplicantResponse;
import com.sgu.student_admission_system.dto.Applicant.ApplicantUpdateRequest;
import com.sgu.student_admission_system.entity.Applicant;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ApplicantMapper {
    Applicant toApplicant(ApplicantCreationRequest request);

    @Mapping(target = "userId", source = "user.id")
    ApplicantResponse toApplicantResponse(Applicant applicant);

    void updateApplicant(@MappingTarget Applicant applicant, ApplicantUpdateRequest request);
}
