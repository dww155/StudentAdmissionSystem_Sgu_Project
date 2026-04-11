package com.sgu.admission_desktop.dto.ConversionRule;

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
public class ConversionRuleResponse {

    Integer id;
    String method;
    String subjectCombination;
    String subject;
    BigDecimal diemA;
    BigDecimal diemB;
    BigDecimal diemC;
    BigDecimal diemD;
    String conversionCode;
    String percentile;
}
