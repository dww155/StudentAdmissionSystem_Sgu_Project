package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemthixettuyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExamScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemthi")
    Integer id;

    @OneToOne(optional = false)
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", nullable = false, unique = true)
    Applicant applicant;

    @Column(name = "sobaodanh", length = 45)
    String registrationNumber;

    @Column(name = "d_phuongthuc", length = 10)
    String method;

    @Column(name = "TOA", precision = 8, scale = 2)
    BigDecimal to;

    @Column(name = "LI", precision = 8, scale = 2)
    BigDecimal li;

    @Column(name = "HO", precision = 8, scale = 2)
    BigDecimal ho;

    @Column(name = "SI", precision = 8, scale = 2)
    BigDecimal si;

    @Column(name = "SU", precision = 8, scale = 2)
    BigDecimal su;

    @Column(name = "DI", precision = 8, scale = 2)
    BigDecimal di;

    @Column(name = "VA", precision = 8, scale = 2)
    BigDecimal va;

    @Column(name = "N1_THI", precision = 8, scale = 2)
    BigDecimal n1Thi;

    @Column(name = "N1_CC", precision = 8, scale = 2)
    BigDecimal n1Cc;

    @Column(name = "CNCN", precision = 8, scale = 2)
    BigDecimal cncn;

    @Column(name = "CNNN", precision = 8, scale = 2)
    BigDecimal cnnn;

    @Column(name = "TI", precision = 8, scale = 2)
    BigDecimal ti;

    @Column(name = "KTPL", precision = 8, scale = 2)
    BigDecimal ktpl;

    @Column(name = "NL1", precision = 8, scale = 2)
    BigDecimal nl1;

    @Column(name = "NK1", precision = 8, scale = 2)
    BigDecimal nk1;

    @Column(name = "NK2", precision = 8, scale = 2)
    BigDecimal nk2;
}
