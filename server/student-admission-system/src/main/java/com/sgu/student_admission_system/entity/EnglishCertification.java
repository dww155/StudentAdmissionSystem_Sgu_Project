package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Table(name = "xt_chungchi_tienganh")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EnglishCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", nullable = false)
    Applicant applicant;

    @Column(name = "certification_name", nullable = false, length = 255)
    String certificationName;

    @Column(name = "certification_level_score", precision = 5, scale = 2, nullable = false)
    String certificationScore;

    @Column(name = "conversion_score", precision = 5, scale = 2)
    BigDecimal conversionScore;

    @Column(name = "bonus_score", precision = 5, scale = 2)
    BigDecimal bonusScore;
}