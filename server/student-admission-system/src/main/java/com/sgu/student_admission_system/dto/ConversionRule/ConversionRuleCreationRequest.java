package com.sgu.student_admission_system.dto.ConversionRule;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversionRuleCreationRequest {

    String method;
    String subjectCombination;

    @NotBlank(message = "INVALID_SUBJECT")
    String subject;

    BigDecimal diemA;
    BigDecimal diemB;
    BigDecimal diemC;
    BigDecimal diemD;

    String conversionCode;
    String percentile;
}
