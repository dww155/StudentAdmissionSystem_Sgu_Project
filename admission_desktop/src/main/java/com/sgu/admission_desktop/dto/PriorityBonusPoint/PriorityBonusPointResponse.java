package com.sgu.admission_desktop.dto.PriorityBonusPoint;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
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
