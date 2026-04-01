package com.sgu.student_admission_system.dto.AdmissionBonusScore;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionBonusScoreCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    String majorCode;
    String subjectCombinationCode;
    String method;
    BigDecimal bonusScore;
    BigDecimal priorityScore;
    BigDecimal totalScore;
    String note;
    String dcKeys;
}
