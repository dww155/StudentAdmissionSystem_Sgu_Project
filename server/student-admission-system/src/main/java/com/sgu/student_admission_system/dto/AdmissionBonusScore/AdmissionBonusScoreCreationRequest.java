package com.sgu.student_admission_system.dto.AdmissionBonusScore;

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
public class AdmissionBonusScoreCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    @NotBlank(message = "INVALID_MAJOR_CODE")
    String majorCode;

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_CODE")
    String subjectCombinationCode;

    @NotBlank(message = "INVALID_METHOD")
    String method;

    @NotNull(message = "INVALID_BONUS_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_BONUS_SCORE")
    BigDecimal bonusScore;

    @NotNull(message = "INVALID_PRIORITY_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_PRIORITY_SCORE")
    BigDecimal priorityScore;

    @NotNull(message = "INVALID_TOTAL_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_TOTAL_SCORE")
    BigDecimal totalScore;

    String note;

    @NotBlank(message = "INVALID_DC_KEYS")
    String dcKeys;
}
