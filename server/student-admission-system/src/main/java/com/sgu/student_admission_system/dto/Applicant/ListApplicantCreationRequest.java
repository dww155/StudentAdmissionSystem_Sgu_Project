package com.sgu.student_admission_system.dto.Applicant;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListApplicantCreationRequest {
    @Valid
    List<ApplicantCreationRequest> applicantCreationRequestList;
}