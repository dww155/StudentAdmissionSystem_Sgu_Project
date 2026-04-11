package com.sgu.admission_desktop.dto.SubjectCombination;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_NAME")
    String name;
}
