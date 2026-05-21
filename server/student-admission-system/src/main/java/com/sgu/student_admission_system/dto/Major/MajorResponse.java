package com.sgu.student_admission_system.dto.Major;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sgu.student_admission_system.dto.MajorSubjectGroup.MajorSubjectGroupResponse;
import com.sgu.student_admission_system.entity.MajorSubjectGroup;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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
    Long admissionPreferenceCount;

    List<String> majorSubjectGroups;
}
