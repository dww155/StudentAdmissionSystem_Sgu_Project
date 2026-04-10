package com.sgu.student_admission_system.dto.AdmissionPreference;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionPreferenceCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    @NotBlank(message = "INVALID_MAJOR_CODE")
    String majorCode;

    @NotNull(message = "INVALID_PRIORITY_ORDER")
    @Min(value = 1, message = "INVALID_PRIORITY_ORDER")
    Integer priorityOrder;

    @NotBlank(message = "INVALID_NV_KEYS")
    String nvKeys;

    // OPTIONAL (có thể null)
    String method;

    // OPTIONAL (có thể null)
    String subjectGroup;
}