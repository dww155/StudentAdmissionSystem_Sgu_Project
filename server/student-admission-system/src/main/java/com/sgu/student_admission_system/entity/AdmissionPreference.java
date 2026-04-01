package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_nguyenvongxettuyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idnv")
    Integer id;

    @Column(name = "nn_cccd", nullable = false, length = 45)
    String cccd;

    @Column(name = "nv_manganh", nullable = false, length = 45)
    String majorCode;

    @Column(name = "nv_tt", nullable = false)
    Integer priorityOrder;

    @Column(name = "diem_thxt", precision = 10, scale = 5)
    BigDecimal examScore;

    @Column(name = "diem_utqd", precision = 10, scale = 5)
    BigDecimal conversionPriorityScore;

    @Column(name = "diem_cong", precision = 6, scale = 2)
    BigDecimal bonusScore;

    @Column(name = "diem_xettuyen", precision = 10, scale = 5)
    BigDecimal admissionScore;

    @Column(name = "nv_ketqua", length = 45)
    String result;

    @Column(name = "nv_keys", unique = true, length = 45)
    String nvKeys;

    @Column(name = "tt_phuongthuc", length = 45)
    String method;

    @Column(name = "tt_thm", length = 45)
    String subjectGroup;
}



