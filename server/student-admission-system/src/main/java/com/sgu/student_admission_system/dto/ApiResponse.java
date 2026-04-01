package com.sgu.student_admission_system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sgu.student_admission_system.exception.ErrorCode;
import lombok.*;

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

    //    ERROR
    public ApiResponse(ErrorCode errorCode) {
        this.success = false;
        this.message = errorCode.getMessage();
        this.data = null;
        this.errorCode = errorCode.getErrorCode();
        timestamp = Instant.now();
        this.data = null;
    }
}