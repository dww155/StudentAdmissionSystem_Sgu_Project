package com.sgu.student_admission_system.dto.AdmissionPreference;

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
public class AdmissionPreferenceResponse {

    Integer id;
    String cccd;
    String majorCode;

    Integer priorityOrder;
    BigDecimal examScore;
    BigDecimal conversionPriorityScore;
    BigDecimal bonusScore;
    BigDecimal admissionScore;

    String result;
    String nvKeys;
    String method;
    String subjectGroup;
}
