package com.sgu.student_admission_system.dto.Major;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MajorResponse {

    Integer id;
    String majorCode;
    String majorName;
    String baseCombination;
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
