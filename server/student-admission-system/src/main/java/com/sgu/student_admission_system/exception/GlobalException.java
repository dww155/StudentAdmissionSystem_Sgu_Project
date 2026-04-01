package com.sgu.student_admission_system.exception;

import com.sgu.student_admission_system.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalException {
    @ExceptionHandler(AppException.class)
    ResponseEntity<ApiResponse> appExceptionHandler(AppException appException) {
        ErrorCode errorCode = appException.getErrorCode();

        ApiResponse apiResponse = new ApiResponse(errorCode);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ApiResponse> dataIntegrityViolationHandler(
            DataIntegrityViolationException exception
    ) {
        log.error("Data integrity violation", exception);

        ErrorCode errorCode = ErrorCode.USERNAME_ALREADY_EXISTS;

        ApiResponse apiResponse = new ApiResponse(errorCode);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception
    ) {
        String message = exception
                .getBindingResult()
                .getFieldError()
                .getDefaultMessage();

        ErrorCode errorCode;
        try {
            errorCode = ErrorCode.valueOf(message);
        } catch (Exception e) {
            errorCode = ErrorCode.UNCATEGORIZED;
        }

        ApiResponse apiResponse = new ApiResponse(errorCode);

        return ResponseEntity
                .status(errorCode.getHttpStatus())
                .body(apiResponse);
    }
}
