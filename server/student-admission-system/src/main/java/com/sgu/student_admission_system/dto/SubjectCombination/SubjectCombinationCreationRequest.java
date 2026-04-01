package com.sgu.student_admission_system.dto.SubjectCombination;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectCombinationCreationRequest {

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_CODE")
    String code;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon1;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon2;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon3;

    String name;
}
