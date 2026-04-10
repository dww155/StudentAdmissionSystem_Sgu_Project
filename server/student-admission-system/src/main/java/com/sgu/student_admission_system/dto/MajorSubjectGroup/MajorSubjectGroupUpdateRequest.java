package com.sgu.student_admission_system.dto.MajorSubjectGroup;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorSubjectGroupUpdateRequest {

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_CODE")
    String subjectCombinationCode;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon1;

    @NotNull(message = "INVALID_SUBJECT_WEIGHT")
    @Min(value = 0, message = "INVALID_SUBJECT_WEIGHT")
    Integer subject1Weight;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon2;

    @NotNull(message = "INVALID_SUBJECT_WEIGHT")
    @Min(value = 0, message = "INVALID_SUBJECT_WEIGHT")
    Integer subject2Weight;

    @NotBlank(message = "INVALID_SUBJECT")
    String mon3;

    @NotNull(message = "INVALID_SUBJECT_WEIGHT")
    @Min(value = 0, message = "INVALID_SUBJECT_WEIGHT")
    Integer subject3Weight;

    @NotBlank(message = "INVALID_KEY_CODE")
    String keyCode;
    Boolean n1;
    Boolean to;
    Boolean li;
    Boolean ho;
    Boolean si;
    Boolean va;
    Boolean su;
    Boolean di;
    Boolean ti;
    Boolean other;
    Boolean ktpl;
    @NotNull(message = "INVALID_DEVIATION")
    @DecimalMin(value = "0.0", message = "INVALID_DEVIATION")
    BigDecimal deviation;
}
