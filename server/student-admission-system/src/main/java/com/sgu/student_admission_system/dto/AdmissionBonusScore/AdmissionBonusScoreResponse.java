package com.sgu.student_admission_system.dto.AdmissionBonusScore;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AdmissionBonusScoreResponse {

    Integer id;
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
