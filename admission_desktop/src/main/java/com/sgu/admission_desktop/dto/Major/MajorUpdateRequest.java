package com.sgu.admission_desktop.dto.Major;

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
public class MajorUpdateRequest {

    @NotBlank(message = "INVALID_MAJOR_NAME")
    String majorName;

    String baseCombination;

    @NotNull(message = "INVALID_QUOTA")
    @Min(value = 0, message = "INVALID_QUOTA")
    Integer quota;

    @NotNull(message = "INVALID_FLOOR_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_FLOOR_SCORE")
    BigDecimal floorScore;

    @NotNull(message = "INVALID_ADMISSION_SCORE")
    @DecimalMin(value = "0.0", message = "INVALID_ADMISSION_SCORE")
    BigDecimal admissionScore;

    @NotBlank(message = "INVALID_DIRECT_ADMISSION")
    String directAdmission;

    @NotBlank(message = "INVALID_DGNL")
    String dgnl;

    @NotBlank(message = "INVALID_THPT")
    String thpt;

    @NotBlank(message = "INVALID_VSAT")
    String vsat;

    @NotNull(message = "INVALID_DIRECT_ADMISSION_COUNT")
    @Min(value = 0, message = "INVALID_DIRECT_ADMISSION_COUNT")
    Integer directAdmissionCount;

    @NotNull(message = "INVALID_COMPETENCY_EXAM_COUNT")
    @Min(value = 0, message = "INVALID_COMPETENCY_EXAM_COUNT")
    Integer competencyExamCount;

    @NotNull(message = "INVALID_VSAT_COUNT")
    @Min(value = 0, message = "INVALID_VSAT_COUNT")
    Integer vsatCount;

    @NotBlank(message = "INVALID_HIGH_SCHOOL_EXAM_COUNT")
    String highSchoolExamCount;
}
