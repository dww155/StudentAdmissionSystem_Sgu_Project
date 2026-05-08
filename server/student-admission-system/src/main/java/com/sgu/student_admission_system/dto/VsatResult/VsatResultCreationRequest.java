package com.sgu.student_admission_system.dto.VsatResult;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VsatResultCreationRequest {

    @NotBlank(message = "INVALID_CCCD")
    String cccd;

    @NotNull(message = "INVALID_DOT_THI")
    @Min(value = 0, message = "INVALID_DOT_THI")
    Integer dotThi;

    @NotBlank(message = "INVALID_MA_DOT_THI")
    String maDotThi;

    @NotNull(message = "INVALID_NGAY_THI")
    LocalDate ngayThi;

    @NotNull(message = "INVALID_NAM_THI")
    @Min(value = 0, message = "INVALID_NAM_THI")
    Integer namThi;

    @NotBlank(message = "INVALID_MA_MON_THI")
    String maMonThi;

    @NotBlank(message = "INVALID_TEN_MON_THI")
    String tenMonThi;

    @NotNull(message = "INVALID_DIEM")
    @DecimalMin(value = "0.0", message = "INVALID_DIEM")
    Double diem;

    @NotNull(message = "INVALID_THANG_DIEM")
    @Min(value = 1, message = "INVALID_THANG_DIEM")
    Integer thangDiem;

    @NotBlank(message = "INVALID_MA_DVTCTDL")
    String maDvtctdl;

    @NotBlank(message = "INVALID_TEN_DVTCTDL")
    String tenDvtctdl;
}
