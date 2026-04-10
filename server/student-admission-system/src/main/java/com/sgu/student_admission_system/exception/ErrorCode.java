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
    INVALID_USERNAME("Invalid username", 1010, HttpStatus.BAD_REQUEST),
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
    INVALID_APPLICANT_LIST("Invalid applicant list", 1212, HttpStatus.BAD_REQUEST),
    INVALID_REGISTRATION_NUMBER("Invalid registration number", 1213, HttpStatus.BAD_REQUEST),

    ADMISSION_PREFERENCE_NOT_FOUND("Admission preference not found", 1301, HttpStatus.NOT_FOUND),
    DUPLICATE_ADMISSION_PREFERENCE_PRIORITY("Duplicate preference priority", 1302, HttpStatus.BAD_REQUEST),
    INVALID_EXAM_SCORE("Invalid exam score", 1303, HttpStatus.BAD_REQUEST),
    INVALID_CONVERSION_PRIORITY_SCORE("Invalid conversion priority score", 1304, HttpStatus.BAD_REQUEST),
    INVALID_BONUS_SCORE("Invalid bonus score", 1305, HttpStatus.BAD_REQUEST),
    INVALID_ADMISSION_SCORE("Invalid admission score", 1306, HttpStatus.BAD_REQUEST),
    INVALID_RESULT("Invalid result", 1307, HttpStatus.BAD_REQUEST),
    INVALID_NV_KEYS("Invalid nv keys", 1308, HttpStatus.BAD_REQUEST),
    INVALID_METHOD("Invalid method", 1309, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_GROUP("Invalid subject group", 1310, HttpStatus.BAD_REQUEST),
    INVALID_ADMISSION_PREFERENCE_LIST("Invalid admission preference list", 1311, HttpStatus.BAD_REQUEST),

    EXAM_SCORE_NOT_FOUND("Exam score not found", 1401, HttpStatus.NOT_FOUND),
    INVALID_CONVERSION_CODE("Invalid conversion code", 1402, HttpStatus.BAD_REQUEST),
    INVALID_EXAM_SCORE_LIST("Invalid exam score list", 1403, HttpStatus.BAD_REQUEST),

    CONVERSION_RULE_NOT_FOUND("Conversion rule not found", 1501, HttpStatus.NOT_FOUND),
    INVALID_CONVERSION_SCORE_A("Invalid conversion score A", 1502, HttpStatus.BAD_REQUEST),
    INVALID_CONVERSION_SCORE_B("Invalid conversion score B", 1503, HttpStatus.BAD_REQUEST),
    INVALID_CONVERSION_SCORE_C("Invalid conversion score C", 1504, HttpStatus.BAD_REQUEST),
    INVALID_CONVERSION_SCORE_D("Invalid conversion score D", 1505, HttpStatus.BAD_REQUEST),
    INVALID_PERCENTILE("Invalid percentile", 1506, HttpStatus.BAD_REQUEST),
    INVALID_CONVERSION_RULE_LIST("Invalid conversion rule list", 1507, HttpStatus.BAD_REQUEST),

    MAJOR_NOT_FOUND("Major not found", 1601, HttpStatus.NOT_FOUND),
    INVALID_FLOOR_SCORE("Invalid floor score", 1602, HttpStatus.BAD_REQUEST),
    INVALID_DIRECT_ADMISSION("Invalid direct admission", 1603, HttpStatus.BAD_REQUEST),
    INVALID_DGNL("Invalid dgnl", 1604, HttpStatus.BAD_REQUEST),
    INVALID_THPT("Invalid thpt", 1605, HttpStatus.BAD_REQUEST),
    INVALID_VSAT("Invalid vsat", 1606, HttpStatus.BAD_REQUEST),
    INVALID_DIRECT_ADMISSION_COUNT("Invalid direct admission count", 1607, HttpStatus.BAD_REQUEST),
    INVALID_COMPETENCY_EXAM_COUNT("Invalid competency exam count", 1608, HttpStatus.BAD_REQUEST),
    INVALID_VSAT_COUNT("Invalid vsat count", 1609, HttpStatus.BAD_REQUEST),
    INVALID_HIGH_SCHOOL_EXAM_COUNT("Invalid high school exam count", 1610, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_LIST("Invalid major list", 1611, HttpStatus.BAD_REQUEST),

    MAJOR_SUBJECT_GROUP_NOT_FOUND("Major subject group not found", 1701, HttpStatus.NOT_FOUND),
    MAJOR_SUBJECT_GROUP_ALREADY_EXISTS("Major subject group already exists", 1702, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_WEIGHT("Invalid subject weight", 1703, HttpStatus.BAD_REQUEST),
    INVALID_KEY_CODE("Invalid key code", 1704, HttpStatus.BAD_REQUEST),
    INVALID_DEVIATION("Invalid deviation", 1705, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_SUBJECT_GROUP_LIST("Invalid major subject group list", 1706, HttpStatus.BAD_REQUEST),

    SUBJECT_COMBINATION_NOT_FOUND("Subject combination not found", 1801, HttpStatus.NOT_FOUND),
    INVALID_SUBJECT_COMBINATION_NAME("Invalid subject combination name", 1802, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_COMBINATION_LIST("Invalid subject combination list", 1803, HttpStatus.BAD_REQUEST),

    ADMISSION_BONUS_SCORE_NOT_FOUND("Admission bonus score not found", 1901, HttpStatus.NOT_FOUND),
    INVALID_PRIORITY_SCORE("Invalid priority score", 1902, HttpStatus.BAD_REQUEST),
    INVALID_TOTAL_SCORE("Invalid total score", 1903, HttpStatus.BAD_REQUEST),
    INVALID_DC_KEYS("Invalid dc keys", 1904, HttpStatus.BAD_REQUEST),
    INVALID_ADMISSION_BONUS_SCORE_LIST("Invalid admission bonus score list", 1905, HttpStatus.BAD_REQUEST),

    INVALID_APPLICANT_ID("Invalid applicant", 2001, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_ID("Invalid major", 2002, HttpStatus.BAD_REQUEST),
    INVALID_PRIORITY_ORDER("Invalid preference priority order", 2003, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT("Invalid subject", 2004, HttpStatus.BAD_REQUEST),
    INVALID_QUOTA("Invalid quota", 2005, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_CODE("Invalid major code", 2006, HttpStatus.BAD_REQUEST),
    INVALID_MAJOR_NAME("Invalid major name", 2007, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_COMBINATION_ID("Invalid subject combination", 2008, HttpStatus.BAD_REQUEST),
    INVALID_SUBJECT_COMBINATION_CODE("Invalid subject combination code", 2009, HttpStatus.BAD_REQUEST),
    INVALID_TOKEN("Invalid token", 2010, HttpStatus.BAD_REQUEST);

    private final String message;
    private final int errorCode;
    private final HttpStatus httpStatus;
}
