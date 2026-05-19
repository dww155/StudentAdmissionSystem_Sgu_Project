package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_diemconguutien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PriorityBonusPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "cccd",
            referencedColumnName = "cccd",
            nullable = false,
            unique = true
    )
    Applicant applicant;

    @Column(name = "cap", length = 100)
    String level;

    @Column(name = "doi_tuyen", length = 100)
    String team;

    @Column(name = "ma_mon", length = 20)
    String subjectCode;

    @Column(name = "loai_giai", length = 50)
    String prizeType;

    @Column(name = "dc_mon", precision = 4, scale = 2)
    BigDecimal bonusPointForSubject;

    @Column(name = "dc_thxt", precision = 4, scale = 2)
    BigDecimal bonusPointForSubjectGroup;
}
