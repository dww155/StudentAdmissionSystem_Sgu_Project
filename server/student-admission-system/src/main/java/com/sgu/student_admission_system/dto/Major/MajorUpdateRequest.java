package com.sgu.student_admission_system.dto.Major;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorUpdateRequest {

    @NotBlank(message = "INVALID_MAJOR_NAME")
    String majorName;

    String baseCombination;

    @NotNull(message = "INVALID_QUOTA")
    @Min(value = 0, message = "INVALID_QUOTA")
    Integer quota;

    BigDecimal floorScore;
    BigDecimal admissionScore;
    String directAdmission;
    String dgnl;
    String thpt;
    String vsat;
    Integer directAdmissionCount;
    Integer competencyExamCount;
    Integer vsatCount;
    String highSchoolExamCount;
}
