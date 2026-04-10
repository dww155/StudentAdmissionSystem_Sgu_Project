package com.sgu.admission_desktop.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<DataType> {

    boolean success;
    String message;
    DataType data;
    int errorCode;
    int status;
    Instant timestamp;

    //    SUCCESS
    public ApiResponse(DataType data, String message) {
        this.success = true;
        this.message = message;
        this.data = data;
        this.status = 1000;
        timestamp = Instant.now();
    }
}