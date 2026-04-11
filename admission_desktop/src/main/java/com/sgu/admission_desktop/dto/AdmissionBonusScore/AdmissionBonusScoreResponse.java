package com.sgu.admission_desktop.dto.AdmissionBonusScore;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
