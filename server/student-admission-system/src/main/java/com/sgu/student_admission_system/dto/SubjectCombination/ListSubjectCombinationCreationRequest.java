package com.sgu.student_admission_system.dto.SubjectCombination;

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
public class ListSubjectCombinationCreationRequest {
    @NotEmpty(message = "INVALID_SUBJECT_COMBINATION_LIST")
    @Valid
    List<SubjectCombinationCreationRequest> subjectCombinationCreationRequestList;
}
