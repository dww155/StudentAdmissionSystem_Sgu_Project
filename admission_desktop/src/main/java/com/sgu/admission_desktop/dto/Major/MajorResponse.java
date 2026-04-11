package com.sgu.admission_desktop.dto.Major;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
