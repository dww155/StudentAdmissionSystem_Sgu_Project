package com.sgu.student_admission_system.dto.AdmissionPreference;

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
public class ListAdmissionPreferenceCreationRequest {
    @NotEmpty(message = "INVALID_ADMISSION_PREFERENCE_LIST")
    @Valid
    List<AdmissionPreferenceCreationRequest> admissionPreferenceCreationRequestList;
}
