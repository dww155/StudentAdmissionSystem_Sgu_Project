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
public class AdmissionPreferenceUpdateRequest {

    @NotBlank(message = "INVALID_MAJOR_CODE")
    String majorCode;

    @NotNull(message = "INVALID_PRIORITY_ORDER")
    @Min(value = 1, message = "INVALID_PRIORITY_ORDER")
    Integer priorityOrder;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal examScore;

    @NotNull(message = "INVALID_CONVERSION_PRIORITY_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_CONVERSION_PRIORITY_SCORE")
    BigDecimal conversionPriorityScore;

    @NotNull(message = "INVALID_BONUS_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_BONUS_SCORE")
    BigDecimal bonusScore;

    @NotNull(message = "INVALID_ADMISSION_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_ADMISSION_SCORE")
    BigDecimal admissionScore;

    @NotBlank(message = "INVALID_RESULT")
    String result;

    @NotBlank(message = "INVALID_NV_KEYS")
    String nvKeys;

    @NotBlank(message = "INVALID_METHOD")
    String method;

    @NotBlank(message = "INVALID_SUBJECT_GROUP")
    String subjectGroup;
}
