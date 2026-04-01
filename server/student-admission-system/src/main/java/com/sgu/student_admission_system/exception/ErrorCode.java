package com.sgu.student_admission_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // ===== SYSTEM =====
    UNCATEGORIZED("Lỗi không xác định", 9999, HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("Lỗi hệ thống", 5000, HttpStatus.INTERNAL_SERVER_ERROR),

    // ===== COMMON =====
    BAD_REQUEST("Yêu cầu không hợp lệ", 4000, HttpStatus.BAD_REQUEST),
    NOT_FOUND("Không tìm thấy dữ liệu", 4004, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Chưa xác thực", 4001, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Không có quyền truy cập", 4003, HttpStatus.FORBIDDEN),

    // ===== USER / AUTH =====
    USER_NOT_FOUND("Người dùng không tồn tại", 1001, HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("Tên đăng nhập đã tồn tại", 1002, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("Email đã tồn tại", 1003, HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("Số điện thoại đã tồn tại", 1004, HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("Role không tồn tại", 1005, HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Mật khẩu không hợp lệ", 1006, HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("Mật khẩu xác nhận không khớp", 1007, HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED("Tài khoản đã bị vô hiệu hóa", 1008, HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED("Tài khoản đã bị khóa", 1009, HttpStatus.FORBIDDEN),

    // ===== APPLICANT =====
    APPLICANT_NOT_FOUND("Thí sinh không tồn tại", 1201, HttpStatus.NOT_FOUND),

    INVALID_CCCD("CCCD không hợp lệ", 1202, HttpStatus.BAD_REQUEST),
    INVALID_LAST_NAME("Họ không hợp lệ", 1203, HttpStatus.BAD_REQUEST),
    INVALID_FIRST_NAME("Tên không hợp lệ", 1204, HttpStatus.BAD_REQUEST),
    INVALID_DATE_OF_BIRTH("Ngày sinh không hợp lệ", 1205, HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("Số điện thoại không hợp lệ", 1206, HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("Email không hợp lệ", 1207, HttpStatus.BAD_REQUEST),
    INVALID_GENDER("Giới tính không hợp lệ", 1208, HttpStatus.BAD_REQUEST),
    INVALID_BIRTH_PLACE("Nơi sinh không hợp lệ", 1209, HttpStatus.BAD_REQUEST),
    INVALID_APPLICANT_TYPE("Đối tượng không hợp lệ", 1210, HttpStatus.BAD_REQUEST),
    INVALID_REGION("Khu vực không hợp lệ", 1211, HttpStatus.BAD_REQUEST);

    private final String message;
    private final int errorCode;
    private final HttpStatus httpStatus;
}