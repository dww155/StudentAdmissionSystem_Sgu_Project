package com.sgu.student_admission_system.dto.EnglishCertification;

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
public class EnglishCertificationResponse {

    Long id;
    String cccd;

    String certificationName;
    BigDecimal certificationScore;
    BigDecimal conversionScore;
    BigDecimal bonusScore;
}
