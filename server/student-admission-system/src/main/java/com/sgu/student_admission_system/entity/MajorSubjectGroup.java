package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_nganh_tohop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MajorSubjectGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "manganh", referencedColumnName = "manganh", nullable = false)
    Major major;

    @ManyToOne(optional = false)
    @JoinColumn(name = "matohop", referencedColumnName = "matohop", nullable = false)
    SubjectCombination subjectCombination;

    @Column(name = "th_mon1", length = 10)
    String mon1;

    @Column(name = "hsmon1")
    Integer subject1Weight;

    @Column(name = "th_mon2", length = 10)
    String mon2;

    @Column(name = "hsmon2")
    Integer subject2Weight;

    @Column(name = "th_mon3", length = 10)
    String mon3;

    @Column(name = "hsmon3")
    Integer subject3Weight;

    @Column(name = "tb_keys", unique = true, length = 45)
    String keyCode;

    @Column(name = "N1")
    Boolean n1;

    @Column(name = "TOA")
    Boolean to;

    @Column(name = "LI")
    Boolean li;

    @Column(name = "HO")
    Boolean ho;

    @Column(name = "SI")
    Boolean si;

    @Column(name = "VA")
    Boolean va;

    @Column(name = "SU")
    Boolean su;

    @Column(name = "DI")
    Boolean di;

    @Column(name = "TI")
    Boolean ti;

    @Column(name = "KHAC")
    Boolean other;

    @Column(name = "KTPL")
    Boolean ktpl;

    @Column(name = "dolech", precision = 6, scale = 2)
    BigDecimal deviation;
}
