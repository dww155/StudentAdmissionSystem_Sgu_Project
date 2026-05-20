package com.sgu.student_admission_system.dto.PriorityBonusPoint;

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
public class PriorityBonusPointResponse {
    Integer id;
    String cccd;

    String level;
    String team;
    String subjectCode;
    String prizeType;
    BigDecimal bonusPointForSubject;
    BigDecimal bonusPointForSubjectGroup;
}
