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

    @ManyToOne(optional = false)
    @JoinColumn(name = "ts_cccd", referencedColumnName = "cccd", nullable = false)
    Applicant applicant;

    @ManyToOne
    @JoinColumn(name = "manganh", referencedColumnName = "manganh")
    Major major;

    @ManyToOne
    @JoinColumn(name = "matohop", referencedColumnName = "matohop")
    SubjectCombination subjectCombination;

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



