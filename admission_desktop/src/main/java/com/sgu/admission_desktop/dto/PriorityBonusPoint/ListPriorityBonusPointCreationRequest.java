package com.sgu.admission_desktop.dto.PriorityBonusPoint;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ListPriorityBonusPointCreationRequest {
    List<PriorityBonusPointCreationRequest> priorityBonusPointCreationRequestList;
}
