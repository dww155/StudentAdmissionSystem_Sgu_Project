package com.sgu.student_admission_system.dto.ExamScore;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamScoreUpdateRequest {

    String conversionCode;

    String registrationNumber;
    String method;
    BigDecimal to;
    BigDecimal li;
    BigDecimal ho;
    BigDecimal si;
    BigDecimal su;
    BigDecimal di;
    BigDecimal va;
    BigDecimal n1Thi;
    BigDecimal n1Cc;
    BigDecimal cncn;
    BigDecimal cnnn;
    BigDecimal ti;
    BigDecimal ktpl;
    BigDecimal nl1;
    BigDecimal nk1;
    BigDecimal nk2;
}
