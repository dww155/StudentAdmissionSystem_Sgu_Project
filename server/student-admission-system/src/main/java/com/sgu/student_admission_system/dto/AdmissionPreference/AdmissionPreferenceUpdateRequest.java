package com.sgu.student_admission_system.dto.AdmissionPreference;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionPreferenceUpdateRequest {

    @NotBlank(message = "INVALID_MAJOR_CODE")
    String majorCode;

    @NotNull(message = "INVALID_PRIORITY_ORDER")
    @Min(value = 1, message = "INVALID_PRIORITY_ORDER")
    Integer priorityOrder;

    BigDecimal examScore;
    BigDecimal conversionPriorityScore;
    BigDecimal bonusScore;
    BigDecimal admissionScore;
    String result;
    String nvKeys;
    String method;
    String subjectGroup;
}
