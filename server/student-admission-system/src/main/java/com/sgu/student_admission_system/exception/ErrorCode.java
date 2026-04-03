package com.sgu.student_admission_system.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNCATEGORIZED("Uncategorized error", 9999, HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("Internal server error", 5000, HttpStatus.INTERNAL_SERVER_ERROR),

    BAD_REQUEST("Bad request", 4000, HttpStatus.BAD_REQUEST),
    NOT_FOUND("Resource not found", 4004, HttpStatus.NOT_FOUND),
    UNAUTHORIZED("Unauthorized", 4001, HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Forbidden", 4003, HttpStatus.FORBIDDEN),

    USER_NOT_FOUND("User not found", 1001, HttpStatus.NOT_FOUND),
    USERNAME_ALREADY_EXISTS("Username already exists", 1002, HttpStatus.BAD_REQUEST),
    EMAIL_ALREADY_EXISTS("Email already exists", 1003, HttpStatus.BAD_REQUEST),
    PHONE_ALREADY_EXISTS("Phone number already exists", 1004, HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND("Role not found", 1005, HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("Invalid password", 1006, HttpStatus.BAD_REQUEST),
    PASSWORD_MISMATCH("Password mismatch", 1007, HttpStatus.BAD_REQUEST),
    ACCOUNT_DISABLED("Account disabled", 1008, HttpStatus.FORBIDDEN),
    ACCOUNT_LOCKED("Account locked", 1009, HttpStatus.FORBIDDEN),

    APPLICANT_NOT_FOUND("Applicant not found", 1201, HttpStatus.NOT_FOUND),
    INVALID_CCCD("Invalid CCCD", 1202, HttpStatus.BAD_REQUEST),
    INVALID_LAST_NAME("Invalid last name", 1203, HttpStatus.BAD_REQUEST),
    INVALID_FIRST_NAME("Invalid first name", 1204, HttpStatus.BAD_REQUEST),
    INVALID_DATE_OF_BIRTH("Invalid date of birth", 1205, HttpStatus.BAD_REQUEST),
    INVALID_PHONE_NUMBER("Invalid phone number", 1206, HttpStatus.BAD_REQUEST),
    INVALID_EMAIL("Invalid email", 1207, HttpStatus.BAD_REQUEST),
    INVALID_GENDER("Invalid gender", 1208, HttpStatus.BAD_REQUEST),
    INVALID_BIRTH_PLACE("Invalid birth place", 1209, HttpStatus.BAD_REQUEST),
    INVALID_APPLICANT_TYPE("Invalid applicant type", 1210, HttpStatus.BAD_REQUEST),
    INVALID_REGION("Invalid region", 1211, HttpStatus.BAD_REQUEST),

    ADMISSION_PREFERENCE_NOT_FOUND("Admission preference not found", 1301, HttpStatus.NOT_FOUND),
    DUPLICATE_ADMISSION_PREFERENCE_PRIORITY("Duplicate preference priority", 1302, HttpStatus.BAD_REQUEST),

    EXAM_SCORE_NOT_FOUND("Exam score not found", 1401, HttpStatus.NOT_FOUND),

    CONVERSION_RULE_NOT_FOUND("Conversion rule not found", 1501, HttpStatus.NOT_FOUND),

    MAJOR_NOT_FOUND("Major not found", 1601, HttpStatus.NOT_FOUND),

    MAJOR_SUBJECT_GROUP_NOT_FOUND("Major subject group not found", 1701, HttpStatus.NOT_FOUND),
    MAJOR_SUBJECT_GROUP_ALREADY_EXISTS("Major subject group already exists", 1702, HttpStatus.BAD_REQUEST),

    SUBJECT_COMBINATION_NOT_FOUND("Subject combination not found", 1801, HttpStatus.NOT_FOUND),

    ADMISSION_BONUS_SCORE_NOT_FOUND("Admission bonus score not found", 1901, HttpStatus.NOT_FOUND),

    INVALID_APPLICANT_ID("Invalid applicant", 2001, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_ID("Invalid major", 2002, HttpStatus.BAD_REQUEST),
    INVALID_PRIORITY_ORDER("Invalid preference priority order", 2003, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT("Invalid subject", 2004, HttpStatus.BAD_REQUEST),
    INVALID_QUOTA("Invalid quota", 2005, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_CODE("Invalid major code", 2006, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_NAME("Invalid major name", 2007, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_COMBINATION_ID("Invalid subject combination", 2008, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_COMBINATION_CODE("Invalid subject combination code", 2009, HttpStatus.BAD_REQUEST);

    private final String message;
    private final int errorCode;
    private final HttpStatus httpStatus;
}
