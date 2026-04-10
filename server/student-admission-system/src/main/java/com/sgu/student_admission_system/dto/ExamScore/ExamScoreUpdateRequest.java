package com.sgu.student_admission_system.dto.ExamScore;

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
public class ExamScoreUpdateRequest {

    @NotBlank(message = "INVALID_CONVERSION_CODE")
    String conversionCode;

    @NotBlank(message = "INVALID_REGISTRATION_NUMBER")
    String registrationNumber;

    @NotBlank(message = "INVALID_METHOD")
    String method;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal to;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal li;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal ho;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal si;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal su;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal di;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal va;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal n1Thi;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal n1Cc;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal cncn;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal cnnn;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal ti;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal ktpl;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal nl1;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal nk1;

    @NotNull(message = "INVALID_EXAM_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_EXAM_SCORE")
    BigDecimal nk2;
}
