package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_nganh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Major {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnganh")
    Integer id;

    @Column(name = "manganh", nullable = false, length = 45, unique = true)
    String majorCode;

    @Column(name = "tennganh", nullable = false, length = 100)
    String majorName;

    @ManyToOne
    @JoinColumn(name = "n_tohopgoc", referencedColumnName = "matohop")
    SubjectCombination baseCombination;

    @Column(name = "n_chitieu", nullable = false)
    Integer quota;

    @Column(name = "n_diemsan", precision = 10, scale = 2)
    BigDecimal floorScore;

    @Column(name = "n_diemtrungtuyen", precision = 10, scale = 2)
    BigDecimal admissionScore;

    @Column(name = "n_tuyenthang", length = 1)
    String directAdmission;

    @Column(name = "n_dgnl", length = 1)
    String dgnl;

    @Column(name = "n_thpt", length = 1)
    String thpt;

    @Column(name = "n_vsat", length = 1)
    String vsat;

    @Column(name = "sl_xtt")
    Integer directAdmissionCount;

    @Column(name = "sl_dgnl")
    Integer competencyExamCount;

    @Column(name = "sl_vsat")
    Integer vsatCount;

    @Column(name = "sl_thpt", length = 45)
    String highSchoolExamCount;
}