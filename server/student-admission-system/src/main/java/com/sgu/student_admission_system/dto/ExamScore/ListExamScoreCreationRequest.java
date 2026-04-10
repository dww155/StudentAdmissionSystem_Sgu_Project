package com.sgu.student_admission_system.dto.ExamScore;

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
public class ListExamScoreCreationRequest {
    @NotEmpty(message = "INVALID_EXAM_SCORE_LIST")
    @Valid
    List<ExamScoreCreationRequest> examScoreCreationRequestList;
}
