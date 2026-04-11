package com.sgu.admission_desktop.dto.Applicant;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListApplicantCreationRequest {
    @NotEmpty(message = "INVALID_APPLICANT_LIST")
    @Valid
    List<ApplicantCreationRequest> applicantCreationRequestList;
}
