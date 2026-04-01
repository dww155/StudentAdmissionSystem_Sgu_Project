package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "xt_tohop_monthi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubjectCombination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtohop")
    Integer id;

    @Column(name = "matohop", nullable = false, unique = true, length = 45)
    String code;

    @Column(name = "mon1", nullable = false, length = 10)
    String mon1;

    @Column(name = "mon2", nullable = false, length = 10)
    String mon2;

    @Column(name = "mon3", nullable = false, length = 10)
    String mon3;

    @Column(name = "tentohop", length = 100)
    String name;
}



