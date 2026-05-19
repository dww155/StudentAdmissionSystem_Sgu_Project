package com.sgu.student_admission_system.dto.PriorityBonusPoint;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriorityBonusPointCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    String level;
    String team;
    String subjectCode;
    String prizeType;

    @DecimalMin(value = "0.0", message = "INVALID_PRIORITY_BONUS_POINT_FOR_SUBJECT")
    BigDecimal bonusPointForSubject;

    @DecimalMin(value = "0.0", message = "INVALID_PRIORITY_BONUS_POINT_FOR_SUBJECT_GROUP")
    BigDecimal bonusPointForSubjectGroup;
}
