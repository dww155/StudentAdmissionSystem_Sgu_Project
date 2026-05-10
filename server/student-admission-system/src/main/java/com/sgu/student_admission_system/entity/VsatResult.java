package com.sgu.student_admission_system.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "xt_ketqua_vsat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VsatResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cccd", referencedColumnName = "cccd", nullable = false, unique = false)
    Applicant applicant;

    @Column(name = "dot_thi")
    Integer dotThi;

    @Column(name = "ma_dot_thi", length = 20)
    String maDotThi;

    @Column(name = "ngay_thi")
    LocalDate ngayThi;

    @Column(name = "nam_thi")
    Integer namThi;

    @Column(name = "ma_mon_thi", length = 20)
    String maMonThi;

    @Column(name = "ten_mon_thi", length = 255)
    String tenMonThi;

    @Column(name = "diem")
    BigDecimal diem;

    @Column(name = "thang_diem")
    Integer thangDiem;

    @Column(name = "ma_dvtctdl", length = 20)
    String maDvtctdl;

    @Column(name = "ten_dvtctdl", length = 255)
    String tenDvtctdl;
}
