package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemcongxetuyen")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AdmissionBonusScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddiemcong")
    Integer id;

    @Column(name = "ts_cccd", nullable = false, length = 45)
    String cccd;

    @Column(name = "manganh", length = 20)
    String majorCode;

    @Column(name = "matohop", length = 10)
    String subjectCombinationCode;

    @Column(name = "phuongthuc", length = 45)
    String method;

    @Column(name = "diemCC", precision = 6, scale = 2)
    BigDecimal bonusScore;

    @Column(name = "diemUtxt", precision = 6, scale = 2)
    BigDecimal priorityScore;

    @Column(name = "diemTong", precision = 6, scale = 2)
    BigDecimal totalScore;

    @Lob
    @Column(name = "ghichu")
    String note;

    @Column(name = "dc_keys", nullable = false, unique = true, length = 45)
    String dcKeys;
}



