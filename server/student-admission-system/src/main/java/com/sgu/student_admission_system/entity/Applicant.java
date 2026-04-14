package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Entity
@Table(name = "xt_thisinhxettuyen25")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idthisinh")
    Integer id;

    @Column(name = "cccd", unique = true, length = 20)
    String cccd;

    @Column(name = "sobaodanh", length = 45)
    String registrationNumber;

    @Column(name = "ho", length = 100)
    String lastName;

    @Column(name = "ten", length = 100)
    String firstName;

    @Column(name = "ngay_sinh")
    LocalDate dateOfBirth;

    @Column(name = "dien_thoai", length = 20, unique = true)
    String phoneNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    User user;

    @Column(name = "gioi_tinh", length = 10)
    String gender;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "noi_sinh", length = 255)
    String birthPlace;

    @Column(name = "updated_at")
    LocalDate updatedAt;

    @Column(name = "doi_tuong", length = 45)
    String applicantType;

    @Column(name = "khu_vuc", length = 45)
    String region;

    public String getFullName() {
        return (lastName != null ? lastName : "") + " " + (firstName != null ? firstName : "");
    }

    @PreUpdate
    public void update() {
        updatedAt = LocalDate.now();
    }
}
