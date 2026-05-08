package com.sgu.admission_desktop.dto.VsatResult;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class VsatResultResponse {

    Long id;
    String cccd;

    Integer dotThi;
    String maDotThi;
    LocalDate ngayThi;
    Integer namThi;
    String maMonThi;
    String tenMonThi;
    Double diem;
    Integer thangDiem;
    String maDvtctdl;
    String tenDvtctdl;
}
