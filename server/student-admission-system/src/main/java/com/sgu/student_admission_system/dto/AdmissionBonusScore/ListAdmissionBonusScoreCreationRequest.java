package com.sgu.student_admission_system.dto.AdmissionBonusScore;

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
public class ListAdmissionBonusScoreCreationRequest {
    @NotEmpty(message = "INVALID_ADMISSION_BONUS_SCORE_LIST")
    @Valid
    List<AdmissionBonusScoreCreationRequest> admissionBonusScoreCreationRequestList;
}
