package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_bangquydoi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversionRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idqd")
    Integer id;

    @Column(name = "d_phuongthuc", length = 45)
    String method;

    @Column(name = "d_tohop", length = 45)
    String subjectCombination;

    @Column(name = "d_mon", length = 45)
    String subject;

    @Column(name = "d_diema", precision = 6, scale = 2)
    BigDecimal diemA;

    @Column(name = "d_diemb", precision = 6, scale = 2)
    BigDecimal diemB;

    @Column(name = "d_diemc", precision = 6, scale = 2)
    BigDecimal diemC;

    @Column(name = "d_diemd", precision = 6, scale = 2)
    BigDecimal diemD;

    @Column(name = "d_maquydoi", unique = true, length = 45)
    String conversionCode;

    @Column(name = "d_phanvi", length = 45)
    String percentile;
}



