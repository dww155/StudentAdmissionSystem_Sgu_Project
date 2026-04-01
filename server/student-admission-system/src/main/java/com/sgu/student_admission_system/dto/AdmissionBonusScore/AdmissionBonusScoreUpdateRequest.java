package com.sgu.student_admission_system.dto.AdmissionBonusScore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionBonusScoreUpdateRequest {

    String majorCode;
    String subjectCombinationCode;
    String method;
    BigDecimal bonusScore;
    BigDecimal priorityScore;
    BigDecimal totalScore;
    String note;
    String dcKeys;
}
