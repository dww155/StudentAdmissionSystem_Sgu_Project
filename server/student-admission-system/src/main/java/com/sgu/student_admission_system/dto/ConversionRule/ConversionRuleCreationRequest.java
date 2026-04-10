package com.sgu.student_admission_system.dto.ConversionRule;

import jakarta.validation.constraints.DecimalMin;
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
public class ConversionRuleCreationRequest {

    @NotBlank(message = "INVALID_METHOD")
    String method;

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_CODE")
    String subjectCombination;

    @NotBlank(message = "INVALID_SUBJECT")
    String subject;

    @NotNull(message = "INVALID_CONVERSION_SCORE_A")
    @DecimalMin(value = "0.0", message = "INVALID_CONVERSION_SCORE_A")
    BigDecimal diemA;

    @NotNull(message = "INVALID_CONVERSION_SCORE_B")
    @DecimalMin(value = "0.0", message = "INVALID_CONVERSION_SCORE_B")
    BigDecimal diemB;

    @NotNull(message = "INVALID_CONVERSION_SCORE_C")
    @DecimalMin(value = "0.0", message = "INVALID_CONVERSION_SCORE_C")
    BigDecimal diemC;

    @NotNull(message = "INVALID_CONVERSION_SCORE_D")
    @DecimalMin(value = "0.0", message = "INVALID_CONVERSION_SCORE_D")
    BigDecimal diemD;

    @NotBlank(message = "INVALID_CONVERSION_CODE")
    String conversionCode;

    @NotBlank(message = "INVALID_PERCENTILE")
    String percentile;
}
