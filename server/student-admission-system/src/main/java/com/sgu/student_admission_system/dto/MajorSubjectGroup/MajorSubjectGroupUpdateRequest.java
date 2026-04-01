package com.sgu.student_admission_system.dto.MajorSubjectGroup;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorSubjectGroupUpdateRequest {

    @NotBlank(message = "INVALID_SUBJECT_COMBINATION_CODE")
    String subjectCombinationCode;

    String mon1;
    Integer subject1Weight;
    String mon2;
    Integer subject2Weight;
    String mon3;
    Integer subject3Weight;
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
    BigDecimal deviation;
}
