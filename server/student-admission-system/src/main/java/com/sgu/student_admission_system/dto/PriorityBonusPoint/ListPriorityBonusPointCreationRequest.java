package com.sgu.student_admission_system.dto.PriorityBonusPoint;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListPriorityBonusPointCreationRequest {
    @NotEmpty(message = "INVALID_PRIORITY_BONUS_POINT_LIST")
    @Valid
    List<PriorityBonusPointCreationRequest> priorityBonusPointCreationRequestList;
}
