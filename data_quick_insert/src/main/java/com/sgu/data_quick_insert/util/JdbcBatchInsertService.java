package com.sgu.data_quick_insert.util;

import com.sgu.data_quick_insert.model.ImportProfile;
import com.sgu.data_quick_insert.model.InsertResult;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class JdbcBatchInsertService {
    public static final String PROFILE_STUDENTS = "students";
    public static final String PROFILE_MAJORS = "majors";
    public static final String PROFILE_SUBJECTS = "subjects";
    public static final String PROFILE_MAJOR_SUBJECTS = "major_subjects";
    public static final String PROFILE_SCORES = "scores";
    public static final String PROFILE_BONUS = "bonus";
    public static final String PROFILE_WISHES = "wishes";
    public static final String PROFILE_CONVERSION = "conversion";

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/uuuu"),
            DateTimeFormatter.ofPattern("dd/MM/uuuu"),
            DateTimeFormatter.ofPattern("d-M-uuuu"),
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> STUDENT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.optional("registrationNumber", "Registration number", "registration", "ma ts", "mats", "so bao danh", "sbd"),
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD", "citizen id"),
            ExcelImportUtil.ColumnDefinition.optional("fullName", "Ho Ten", "ho ten", "full name", "ten thi sinh"),
            ExcelImportUtil.ColumnDefinition.optional("lastName", "Last name", "lastname", "ho lot", "ho"),
            ExcelImportUtil.ColumnDefinition.optional("firstName", "First name", "firstname", "ten"),
            ExcelImportUtil.ColumnDefinition.required("dateOfBirth", "Ngay sinh", "date of birth", "dob", "ngay sinh"),
            ExcelImportUtil.ColumnDefinition.optional("email", "Email"),
            ExcelImportUtil.ColumnDefinition.optional("phoneNumber", "Phone", "phone", "sdt", "so dien thoai"),
            ExcelImportUtil.ColumnDefinition.required("gender", "Gioi tinh", "gender", "gioi tinh"),
            ExcelImportUtil.ColumnDefinition.optional("applicantType", "DTUT", "applicant type", "doi tuong", "dtut", "doi tuong uu tien"),
            ExcelImportUtil.ColumnDefinition.optional("region", "KVUT", "region", "khu vuc", "kvut", "khu vuc uu tien"),
            ExcelImportUtil.ColumnDefinition.required("birthPlace", "Noi sinh", "birth place", "noi sinh")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> MAJOR_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "Major code", "ma nganh", "ma xet tuyen"),
            ExcelImportUtil.ColumnDefinition.required("majorName", "Major name", "ten nganh", "ten nganh chuong trinh dao tao"),
            ExcelImportUtil.ColumnDefinition.optional("baseCombination", "Base combination", "subject combination code", "to hop goc"),
            ExcelImportUtil.ColumnDefinition.optional("quota", "Quota", "chi tieu"),
            ExcelImportUtil.ColumnDefinition.required("floorScore", "Floor score", "diem san", "nguong dau vao"),
            ExcelImportUtil.ColumnDefinition.optional("admissionScore", "Admission score", "diem trung tuyen"),
            ExcelImportUtil.ColumnDefinition.optional("directAdmission", "Direct admission"),
            ExcelImportUtil.ColumnDefinition.optional("dgnl", "DGNL"),
            ExcelImportUtil.ColumnDefinition.optional("thpt", "THPT"),
            ExcelImportUtil.ColumnDefinition.optional("vsat", "VSAT"),
            ExcelImportUtil.ColumnDefinition.optional("directAdmissionCount", "Direct admission count"),
            ExcelImportUtil.ColumnDefinition.optional("competencyExamCount", "Competency exam count"),
            ExcelImportUtil.ColumnDefinition.optional("vsatCount", "VSAT count"),
            ExcelImportUtil.ColumnDefinition.optional("highSchoolExamCount", "High school exam count")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> SUBJECT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("code", "Code", "ma to hop", "subject combination code"),
            ExcelImportUtil.ColumnDefinition.required("name", "Name", "ten to hop", "subject combination name"),
            ExcelImportUtil.ColumnDefinition.required("mon1", "Subject 1", "mon 1"),
            ExcelImportUtil.ColumnDefinition.required("mon2", "Subject 2", "mon 2"),
            ExcelImportUtil.ColumnDefinition.required("mon3", "Subject 3", "mon 3")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> MAJOR_SUBJECT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "MANGANH", "major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.optional("majorName", "TEN_NGANHCHUAN", "major name", "ten nganh chuan"),
            ExcelImportUtil.ColumnDefinition.required("subjectDefinition", "MA_TO_HOP", "ma to hop"),
            ExcelImportUtil.ColumnDefinition.optional("keyCode", "tb_keys", "key code", "tb keys"),
            ExcelImportUtil.ColumnDefinition.optional("subjectCombinationCode", "TEN_TO_HOP", "subject combination code", "ten to hop"),
            ExcelImportUtil.ColumnDefinition.optional("baseFlag", "Goc", "goc"),
            ExcelImportUtil.ColumnDefinition.required("deviation", "Do lech", "do lech", "deviation")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> SCORE_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.optional("registrationNumber", "Registration number", "registration", "ma ts", "mats", "so bao danh", "sbd"),
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.optional("program", "Chuong trinh", "chuong trinh", "program"),
            ExcelImportUtil.ColumnDefinition.optional("conversionCode", "Conversion code", "ma quy doi"),
            ExcelImportUtil.ColumnDefinition.optional("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.required("to", "TO", "toan", "math"),
            ExcelImportUtil.ColumnDefinition.required("li", "LI", "ly", "physics"),
            ExcelImportUtil.ColumnDefinition.required("ho", "HO", "hoa", "chemistry"),
            ExcelImportUtil.ColumnDefinition.optional("si", "SI", "sinh", "biology"),
            ExcelImportUtil.ColumnDefinition.optional("su", "SU", "history"),
            ExcelImportUtil.ColumnDefinition.optional("di", "DI", "dia", "geography"),
            ExcelImportUtil.ColumnDefinition.optional("va", "VA", "van", "literature"),
            ExcelImportUtil.ColumnDefinition.optional("n1Thi", "NN", "ngoai ngu", "foreign language", "n1 thi"),
            ExcelImportUtil.ColumnDefinition.optional("ktpl", "KTPL", "gdcd", "giao duc cong dan"),
            ExcelImportUtil.ColumnDefinition.optional("ti", "TI", "tieng trung", "chinese"),
            ExcelImportUtil.ColumnDefinition.optional("cncn", "CNCN"),
            ExcelImportUtil.ColumnDefinition.optional("cnnn", "CNNN"),
            ExcelImportUtil.ColumnDefinition.optional("nk1", "NK1"),
            ExcelImportUtil.ColumnDefinition.optional("nk2", "NK2")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> BONUS_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "Major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.required("subjectCombinationCode", "Subject combination code", "ma to hop"),
            ExcelImportUtil.ColumnDefinition.required("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.required("bonusScore", "Bonus score", "diem cong"),
            ExcelImportUtil.ColumnDefinition.required("priorityScore", "Priority score", "diem uu tien"),
            ExcelImportUtil.ColumnDefinition.required("totalScore", "Total score", "tong diem"),
            ExcelImportUtil.ColumnDefinition.required("dcKeys", "DC keys", "dc key"),
            ExcelImportUtil.ColumnDefinition.optional("note", "Note", "ly do")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> WISH_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "Major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.required("priorityOrder", "Priority order", "nguyen vong", "thu tu nguyen vong"),
            ExcelImportUtil.ColumnDefinition.required("nvKeys", "NV keys", "nv key"),
            ExcelImportUtil.ColumnDefinition.optional("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.optional("subjectGroup", "Subject group", "to hop", "subject combination")
    );

    private static final List<ExcelImportUtil.ColumnDefinition> CONVERSION_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.required("subjectCombination", "Subject combination", "subject combination code", "to hop"),
            ExcelImportUtil.ColumnDefinition.required("subject", "Subject", "mon"),
            ExcelImportUtil.ColumnDefinition.required("diemA", "Diem A", "score a"),
            ExcelImportUtil.ColumnDefinition.required("diemB", "Diem B", "score b"),
            ExcelImportUtil.ColumnDefinition.required("diemC", "Diem C", "score c"),
            ExcelImportUtil.ColumnDefinition.required("diemD", "Diem D", "score d"),
            ExcelImportUtil.ColumnDefinition.required("conversionCode", "Conversion code", "ma quy doi"),
            ExcelImportUtil.ColumnDefinition.required("percentile", "Percentile", "bach phan vi")
    );

    private static final List<ImportProfile> PROFILES = List.of(
            new ImportProfile(PROFILE_STUDENTS, "Thi sinh", "xt_thisinhxettuyen25", STUDENT_COLUMNS),
            new ImportProfile(PROFILE_MAJORS, "Nganh", "xt_nganh", MAJOR_COLUMNS),
            new ImportProfile(PROFILE_SUBJECTS, "To hop mon", "xt_tohop_monthi", SUBJECT_COLUMNS),
            new ImportProfile(PROFILE_MAJOR_SUBJECTS, "Nganh - To hop", "xt_nganh_tohop", MAJOR_SUBJECT_COLUMNS),
            new ImportProfile(PROFILE_SCORES, "Diem thi", "xt_diemthixettuyen", SCORE_COLUMNS),
            new ImportProfile(PROFILE_BONUS, "Diem cong", "xt_diemcongxetuyen", BONUS_COLUMNS),
            new ImportProfile(PROFILE_WISHES, "Nguyen vong", "xt_nguyenvongxettuyen", WISH_COLUMNS),
            new ImportProfile(PROFILE_CONVERSION, "Bang quy doi", "xt_bangquydoi", CONVERSION_COLUMNS)
    );

    private JdbcBatchInsertService() {
    }

    public static List<ImportProfile> profiles() {
        return PROFILES;
    }

    public static void testConnection(String jdbcUrl, String username, String password) throws SQLException {
        String normalizedUrl = jdbcUrl == null ? "" : jdbcUrl.trim();
        if (normalizedUrl.isEmpty()) {
            throw new IllegalArgumentException("JDBC URL is required.");
        }
        try (Connection ignored = DriverManager.getConnection(normalizedUrl, username, password)) {
            // Connection successfully opened and closed.
        }
    }

    public static InsertResult importByProfile(
            ImportProfile profile,
            Path excelFilePath,
            String sheetName,
            String tableName,
            String jdbcUrl,
            String username,
            String password,
            int batchSize,
            Consumer<String> logger
    ) throws Exception {
        Objects.requireNonNull(profile, "Import profile is required.");
        Objects.requireNonNull(excelFilePath, "Excel path is required.");
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than 0.");
        }

        String targetTable = trimToNull(tableName);
        if (targetTable == null) {
            targetTable = profile.tableName();
        }

        return switch (profile.key()) {
            case PROFILE_STUDENTS -> importStudents(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_MAJORS -> importMajors(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_SUBJECTS -> importSubjectCombinations(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_MAJOR_SUBJECTS -> importMajorSubjectGroups(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_SCORES -> importScores(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_BONUS -> importBonusScores(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_WISHES -> importWishes(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            case PROFILE_CONVERSION -> importConversionRules(excelFilePath, sheetName, targetTable, jdbcUrl, username, password, batchSize, logger);
            default -> throw new IllegalArgumentException("Unsupported import profile: " + profile.key());
        };
    }

    private static InsertResult importStudents(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("cccd", "sobaodanh", "ho", "ten", "ngay_sinh", "dien_thoai", "gioi_tinh", "email", "noi_sinh", "doi_tuong", "khu_vuc");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, STUDENT_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            String cccd = requireText(rowData.get("cccd"), "CCCD", rowNumber);
            NameParts nameParts = extractNameParts(rowData, rowNumber);
            statement.setString(1, cccd);
            statement.setString(2, firstNonBlank(rowData.get("registrationNumber"), cccd, rowData.get("stt")));
            statement.setString(3, nameParts.lastName());
            statement.setString(4, nameParts.firstName());
            statement.setDate(5, Date.valueOf(parseDateRequired(rowData.get("dateOfBirth"), "Ngay sinh", rowNumber)));
            statement.setString(6, firstNonBlank(rowData.get("phoneNumber"), cccd));
            statement.setString(7, normalizeGenderForDb(rowData.get("gender"), rowNumber));
            statement.setString(8, firstNonBlank(rowData.get("email"), cccd + "@import.local"));
            statement.setString(9, requireText(rowData.get("birthPlace"), "Noi sinh", rowNumber));
            statement.setString(10, firstNonBlank(rowData.get("applicantType"), "0"));
            statement.setString(11, firstNonBlank(rowData.get("region"), "0"));
        }, logger);
    }

    private static InsertResult importMajors(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("manganh", "tennganh", "n_tohopgoc", "n_chitieu", "n_diemsan", "n_diemtrungtuyen", "n_tuyenthang", "n_dgnl", "n_thpt", "n_vsat", "sl_xtt", "sl_dgnl", "sl_vsat", "sl_thpt");
        final String[] defaultBaseCombination = new String[]{null};
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, MAJOR_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            BigDecimal floorScore = parseDecimalRequired(rowData.get("floorScore"), "Floor score", rowNumber);
            String baseCombination = trimToNull(rowData.get("baseCombination"));
            if (baseCombination == null) {
                if (defaultBaseCombination[0] == null) {
                    defaultBaseCombination[0] = findFirstSubjectCombinationCode(connection);
                }
                baseCombination = defaultBaseCombination[0];
            }
            statement.setString(1, requireText(rowData.get("majorCode"), "Major code", rowNumber));
            statement.setString(2, requireText(rowData.get("majorName"), "Major name", rowNumber));
            statement.setString(3, baseCombination);
            statement.setInt(4, parseIntOrDefault(rowData.get("quota"), 0, "Quota", rowNumber));
            statement.setBigDecimal(5, floorScore);
            statement.setBigDecimal(6, parseDecimalOrDefault(rowData.get("admissionScore"), floorScore, "Admission score", rowNumber));
            statement.setString(7, defaultString(rowData.get("directAdmission"), "0"));
            statement.setString(8, defaultString(rowData.get("dgnl"), "0"));
            statement.setString(9, defaultString(rowData.get("thpt"), "0"));
            statement.setString(10, defaultString(rowData.get("vsat"), "0"));
            statement.setInt(11, parseIntOrDefault(rowData.get("directAdmissionCount"), 0, "Direct admission count", rowNumber));
            statement.setInt(12, parseIntOrDefault(rowData.get("competencyExamCount"), 0, "Competency exam count", rowNumber));
            statement.setInt(13, parseIntOrDefault(rowData.get("vsatCount"), 0, "VSAT count", rowNumber));
            statement.setString(14, defaultString(rowData.get("highSchoolExamCount"), "0"));
        }, logger);
    }

    private static InsertResult importSubjectCombinations(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("matohop", "tentohop", "mon1", "mon2", "mon3");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, SUBJECT_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            statement.setString(1, requireText(rowData.get("code"), "Code", rowNumber));
            statement.setString(2, requireText(rowData.get("name"), "Name", rowNumber));
            statement.setString(3, requireText(rowData.get("mon1"), "Subject 1", rowNumber));
            statement.setString(4, requireText(rowData.get("mon2"), "Subject 2", rowNumber));
            statement.setString(5, requireText(rowData.get("mon3"), "Subject 3", rowNumber));
        }, logger);
    }

    private static InsertResult importMajorSubjectGroups(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("manganh", "matohop", "th_mon1", "hsmon1", "th_mon2", "hsmon2", "th_mon3", "hsmon3", "tb_keys", "N1", "TOA", "LI", "HO", "SI", "VA", "SU", "DI", "TI", "KHAC", "KTPL", "dolech");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, MAJOR_SUBJECT_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            ParsedSubjectGroup parsed = parseSubjectDefinition(requireText(rowData.get("subjectDefinition"), "MA_TO_HOP", rowNumber), rowNumber);
            String majorCode = requireText(rowData.get("majorCode"), "MANGANH", rowNumber);
            String subjectCombinationCode = firstNonBlank(rowData.get("subjectCombinationCode"), parsed.subjectCombinationCode());
            String keyCode = firstNonBlank(rowData.get("keyCode"), majorCode + "_" + subjectCombinationCode);
            SubjectFlags flags = buildSubjectFlags(parsed.subjects());
            statement.setString(1, majorCode);
            statement.setString(2, subjectCombinationCode);
            statement.setString(3, parsed.subjects().get(0).subjectCode());
            statement.setInt(4, parsed.subjects().get(0).weight());
            statement.setString(5, parsed.subjects().get(1).subjectCode());
            statement.setInt(6, parsed.subjects().get(1).weight());
            statement.setString(7, parsed.subjects().get(2).subjectCode());
            statement.setInt(8, parsed.subjects().get(2).weight());
            statement.setString(9, keyCode);
            statement.setBoolean(10, flags.n1());
            statement.setBoolean(11, flags.to());
            statement.setBoolean(12, flags.li());
            statement.setBoolean(13, flags.ho());
            statement.setBoolean(14, flags.si());
            statement.setBoolean(15, flags.va());
            statement.setBoolean(16, flags.su());
            statement.setBoolean(17, flags.di());
            statement.setBoolean(18, flags.ti());
            statement.setBoolean(19, flags.other());
            statement.setBoolean(20, flags.ktpl());
            statement.setBigDecimal(21, parseDecimalRequired(rowData.get("deviation"), "Do lech", rowNumber));
        }, logger);
    }

    private static InsertResult importScores(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("cccd", "sobaodanh", "d_phuongthuc", "TOA", "LI", "HO", "SI", "SU", "DI", "VA", "N1_THI", "N1_CC", "CNCN", "CNNN", "TI", "KTPL", "NL1", "NK1", "NK2");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, SCORE_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            String cccd = requireText(rowData.get("cccd"), "CCCD", rowNumber);
            statement.setString(1, cccd);
            statement.setString(2, firstNonBlank(rowData.get("registrationNumber"), cccd, rowData.get("stt")));
            statement.setString(3, firstNonBlank(rowData.get("method"), rowData.get("program"), "THPT"));
            statement.setBigDecimal(4, parseDecimalRequired(rowData.get("to"), "TO", rowNumber));
            statement.setBigDecimal(5, parseDecimalRequired(rowData.get("li"), "LI", rowNumber));
            statement.setBigDecimal(6, parseDecimalRequired(rowData.get("ho"), "HO", rowNumber));
            statement.setBigDecimal(7, parseDecimalOrZero(rowData.get("si"), "SI", rowNumber));
            statement.setBigDecimal(8, parseDecimalOrZero(rowData.get("su"), "SU", rowNumber));
            statement.setBigDecimal(9, parseDecimalOrZero(rowData.get("di"), "DI", rowNumber));
            statement.setBigDecimal(10, parseDecimalOrZero(rowData.get("va"), "VA", rowNumber));
            statement.setBigDecimal(11, parseDecimalOrZero(rowData.get("n1Thi"), "NN", rowNumber));
            statement.setBigDecimal(12, BigDecimal.ZERO);
            statement.setBigDecimal(13, parseDecimalOrZero(rowData.get("cncn"), "CNCN", rowNumber));
            statement.setBigDecimal(14, parseDecimalOrZero(rowData.get("cnnn"), "CNNN", rowNumber));
            statement.setBigDecimal(15, parseDecimalOrZero(rowData.get("ti"), "TI", rowNumber));
            statement.setBigDecimal(16, parseDecimalOrZero(rowData.get("ktpl"), "KTPL", rowNumber));
            statement.setBigDecimal(17, BigDecimal.ZERO);
            statement.setBigDecimal(18, parseDecimalOrZero(rowData.get("nk1"), "NK1", rowNumber));
            statement.setBigDecimal(19, parseDecimalOrZero(rowData.get("nk2"), "NK2", rowNumber));
        }, logger);
    }

    private static InsertResult importBonusScores(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("ts_cccd", "manganh", "matohop", "phuongthuc", "diemCC", "diemUtxt", "diemTong", "ghichu", "dc_keys");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, BONUS_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            statement.setString(1, requireText(rowData.get("cccd"), "CCCD", rowNumber));
            statement.setString(2, requireText(rowData.get("majorCode"), "Major code", rowNumber));
            statement.setString(3, requireText(rowData.get("subjectCombinationCode"), "Subject combination code", rowNumber));
            statement.setString(4, requireText(rowData.get("method"), "Method", rowNumber));
            statement.setBigDecimal(5, parseDecimalRequired(rowData.get("bonusScore"), "Bonus score", rowNumber));
            statement.setBigDecimal(6, parseDecimalRequired(rowData.get("priorityScore"), "Priority score", rowNumber));
            statement.setBigDecimal(7, parseDecimalRequired(rowData.get("totalScore"), "Total score", rowNumber));
            setStringOrNull(statement, 8, trimToNull(rowData.get("note")));
            statement.setString(9, requireText(rowData.get("dcKeys"), "DC keys", rowNumber));
        }, logger);
    }

    private static InsertResult importWishes(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("nn_cccd", "manganh", "nv_tt", "nv_keys", "tt_phuongthuc", "tt_thm");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, WISH_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            statement.setString(1, requireText(rowData.get("cccd"), "CCCD", rowNumber));
            statement.setString(2, requireText(rowData.get("majorCode"), "Major code", rowNumber));
            statement.setInt(3, parseIntRequired(rowData.get("priorityOrder"), "Priority order", rowNumber));
            statement.setString(4, requireText(rowData.get("nvKeys"), "NV keys", rowNumber));
            setStringOrNull(statement, 5, trimToNull(rowData.get("method")));
            setStringOrNull(statement, 6, trimToNull(rowData.get("subjectGroup")));
        }, logger);
    }

    private static InsertResult importConversionRules(Path excelFilePath, String sheetName, String tableName, String jdbcUrl, String username, String password, int batchSize, Consumer<String> logger) throws Exception {
        List<String> dbColumns = List.of("d_phuongthuc", "d_tohop", "d_mon", "d_diema", "d_diemb", "d_diemc", "d_diemd", "d_maquydoi", "d_phanvi");
        return executeMappedImport(excelFilePath, sheetName, tableName, dbColumns, CONVERSION_COLUMNS, jdbcUrl, username, password, batchSize, (connection, statement, rowData, rowNumber) -> {
            statement.setString(1, requireText(rowData.get("method"), "Method", rowNumber));
            statement.setString(2, requireText(rowData.get("subjectCombination"), "Subject combination", rowNumber));
            statement.setString(3, requireText(rowData.get("subject"), "Subject", rowNumber));
            statement.setBigDecimal(4, parseDecimalRequired(rowData.get("diemA"), "Diem A", rowNumber));
            statement.setBigDecimal(5, parseDecimalRequired(rowData.get("diemB"), "Diem B", rowNumber));
            statement.setBigDecimal(6, parseDecimalRequired(rowData.get("diemC"), "Diem C", rowNumber));
            statement.setBigDecimal(7, parseDecimalRequired(rowData.get("diemD"), "Diem D", rowNumber));
            statement.setString(8, requireText(rowData.get("conversionCode"), "Conversion code", rowNumber));
            statement.setString(9, requireText(rowData.get("percentile"), "Percentile", rowNumber));
        }, logger);
    }

    private static InsertResult executeMappedImport(
            Path excelFilePath,
            String sheetName,
            String tableName,
            List<String> dbColumns,
            List<ExcelImportUtil.ColumnDefinition> columnDefinitions,
            String jdbcUrl,
            String username,
            String password,
            int batchSize,
            RowBinder rowBinder,
            Consumer<String> logger
    ) throws Exception {
        String validatedTableName = SqlIdentifierValidator.requireValidTableName(tableName);
        List<String> validatedColumns = SqlIdentifierValidator.requireValidColumns(dbColumns);
        String insertSql = buildInsertSql(validatedTableName, validatedColumns);

        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
            connection.setAutoCommit(false);
            ImportState state = new ImportState(insertSql);

            try (PreparedStatement statement = connection.prepareStatement(insertSql)) {
                ExcelImportUtil.ReadStats readStats = ExcelImportUtil.forEachMappedRow(
                        excelFilePath,
                        sheetName,
                        columnDefinitions,
                        (rowNumber, rowData) -> {
                            rowBinder.bind(connection, statement, rowData, rowNumber);
                            statement.addBatch();
                            state.pendingBatchRows++;
                            state.processedRows++;
                            if (state.pendingBatchRows >= batchSize) {
                                flushBatch(connection, statement, state, logger);
                            }
                        }
                );

                flushBatch(connection, statement, state, logger);
                connection.commit();
                log(logger, "Import completed. Inserted rows: " + state.insertedRows);

                return new InsertResult(
                        state.insertedRows,
                        readStats.skippedRows(),
                        state.processedRows,
                        batchSize,
                        state.insertSql
                );
            } catch (Exception ex) {
                connection.rollback();
                log(logger, "Import failed and transaction was rolled back: " + rootMessage(ex));
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    private static void flushBatch(Connection connection, PreparedStatement statement, ImportState state, Consumer<String> logger) throws SQLException {
        if (state.pendingBatchRows <= 0) {
            return;
        }
        int[] results = statement.executeBatch();
        long affectedRows = toAffectedRowCount(results);
        state.insertedRows += affectedRows;
        state.pendingBatchRows = 0;
        connection.commit();
        log(logger, "Committed batch. Inserted so far: " + state.insertedRows);
    }

    private static long toAffectedRowCount(int[] results) {
        long count = 0;
        for (int result : results) {
            if (result > 0) {
                count += result;
            } else if (result == Statement.SUCCESS_NO_INFO) {
                count += 1;
            }
        }
        return count;
    }

    private static String buildInsertSql(String tableName, List<String> columns) {
        String columnClause = String.join(", ", columns);
        String placeholders = columns.stream().map(column -> "?").collect(Collectors.joining(", "));
        return "INSERT INTO " + tableName + " (" + columnClause + ") VALUES (" + placeholders + ")";
    }

    private static String findFirstSubjectCombinationCode(Connection connection) throws SQLException {
        String sql = "SELECT matohop FROM xt_tohop_monthi ORDER BY idtohop LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql);
             var rs = statement.executeQuery()) {
            if (rs.next()) {
                String value = trimToNull(rs.getString(1));
                if (value != null) {
                    return value;
                }
            }
        }
        throw new IllegalArgumentException("No subject combination is available. Import subject combinations first or provide Base combination.");
    }

    private static NameParts extractNameParts(Map<String, String> rowData, long rowNumber) {
        String fullName = trimToNull(rowData.get("fullName"));
        if (fullName != null) {
            return splitFullName(fullName);
        }
        String lastName = trimToNull(rowData.get("lastName"));
        String firstName = trimToNull(rowData.get("firstName"));
        if (lastName != null && firstName != null) {
            return new NameParts(lastName, firstName);
        }
        throw new IllegalArgumentException("Row " + rowNumber + ": Ho Ten or Last name/First name is required.");
    }

    private static NameParts splitFullName(String fullName) {
        String normalized = fullName.trim().replaceAll("\\s+", " ");
        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace < 0) {
            return new NameParts(normalized, normalized);
        }
        return new NameParts(normalized.substring(0, lastSpace).trim(), normalized.substring(lastSpace + 1).trim());
    }

    private static ParsedSubjectGroup parseSubjectDefinition(String value, long rowNumber) {
        String trimmed = value.trim();
        int openIndex = trimmed.indexOf('(');
        int closeIndex = trimmed.lastIndexOf(')');
        if (openIndex <= 0 || closeIndex <= openIndex) {
            throw new IllegalArgumentException("Row " + rowNumber + ": MA_TO_HOP must look like B03(TO-3,VA-3,SI-1).");
        }

        String subjectCombinationCode = trimmed.substring(0, openIndex).trim();
        String content = trimmed.substring(openIndex + 1, closeIndex).trim();
        String[] parts = content.split(",");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Row " + rowNumber + ": MA_TO_HOP must contain exactly 3 weighted subjects.");
        }

        List<ParsedWeightedSubject> subjects = List.of(
                parseWeightedSubject(parts[0], 1, rowNumber),
                parseWeightedSubject(parts[1], 2, rowNumber),
                parseWeightedSubject(parts[2], 3, rowNumber)
        );
        return new ParsedSubjectGroup(subjectCombinationCode, subjects);
    }

    private static ParsedWeightedSubject parseWeightedSubject(String value, int index, long rowNumber) {
        String trimmed = value.trim();
        int separatorIndex = trimmed.lastIndexOf('-');
        if (separatorIndex <= 0 || separatorIndex == trimmed.length() - 1) {
            throw new IllegalArgumentException("Row " + rowNumber + ": Subject " + index + " must look like TO-3.");
        }
        String subjectCode = trimmed.substring(0, separatorIndex).trim().toUpperCase(Locale.ROOT);
        int weight = parseIntRequired(trimmed.substring(separatorIndex + 1).trim(), "Weight " + index, rowNumber);
        return new ParsedWeightedSubject(subjectCode, weight);
    }

    private static SubjectFlags buildSubjectFlags(List<ParsedWeightedSubject> subjects) {
        boolean n1 = false;
        boolean to = false;
        boolean li = false;
        boolean ho = false;
        boolean si = false;
        boolean va = false;
        boolean su = false;
        boolean di = false;
        boolean ti = false;
        boolean other = false;
        boolean ktpl = false;

        for (ParsedWeightedSubject subject : subjects) {
            String code = subject.subjectCode();
            switch (code) {
                case "N1", "NN" -> n1 = true;
                case "TO" -> to = true;
                case "LI" -> li = true;
                case "HO" -> ho = true;
                case "SI" -> si = true;
                case "VA" -> va = true;
                case "SU" -> su = true;
                case "DI" -> di = true;
                case "TI" -> ti = true;
                case "KTPL", "GDCD" -> ktpl = true;
                default -> other = true;
            }
        }
        return new SubjectFlags(n1, to, li, ho, si, va, su, di, ti, other, ktpl);
    }

    private static void setStringOrNull(PreparedStatement statement, int index, String value) throws SQLException {
        if (value == null) {
            statement.setNull(index, java.sql.Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private static String requireText(String value, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " is required.");
        }
        return trimmed;
    }

    private static int parseIntRequired(String value, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " is required.");
        }
        try {
            return Integer.parseInt(trimmed);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " must be an integer.");
        }
    }

    private static int parseIntOrDefault(String value, int defaultValue, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return defaultValue;
        }
        return parseIntRequired(trimmed, fieldName, rowNumber);
    }

    private static BigDecimal parseDecimalRequired(String value, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " is required.");
        }
        try {
            return new BigDecimal(normalizeDecimal(trimmed));
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " must be a valid number.");
        }
    }

    private static BigDecimal parseDecimalOrDefault(String value, BigDecimal defaultValue, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return defaultValue;
        }
        return parseDecimalRequired(trimmed, fieldName, rowNumber);
    }

    private static BigDecimal parseDecimalOrZero(String value, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            return BigDecimal.ZERO;
        }
        return parseDecimalRequired(trimmed, fieldName, rowNumber);
    }

    private static LocalDate parseDateRequired(String value, String fieldName, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " is required.");
        }
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException ignored) {
                // Try next formatter.
            }
        }
        throw new IllegalArgumentException("Row " + rowNumber + ": " + fieldName + " must follow yyyy-MM-dd or dd/MM/yyyy.");
    }

    private static String normalizeGenderForDb(String value, long rowNumber) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException("Row " + rowNumber + ": Gioi tinh is required.");
        }

        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace('\u0111', 'd')
                .replace('\u0110', 'D')
                .toUpperCase(Locale.ROOT)
                .trim();

        if ("NAM".equals(normalized)) {
            return "NAM";
        }
        if ("NU".equals(normalized)) {
            return "NU";
        }
        throw new IllegalArgumentException("Row " + rowNumber + ": Gioi tinh must be Nam or Nu.");
    }

    private static String defaultString(String value, String defaultValue) {
        String trimmed = trimToNull(value);
        return trimmed == null ? defaultValue : trimmed;
    }

    private static String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static String normalizeDecimal(String value) {
        String normalized = value.replace(" ", "");
        int commaIndex = normalized.lastIndexOf(',');
        int dotIndex = normalized.lastIndexOf('.');
        if (commaIndex >= 0 && dotIndex >= 0) {
            if (commaIndex > dotIndex) {
                return normalized.replace(".", "").replace(',', '.');
            }
            return normalized.replace(",", "");
        }
        if (commaIndex >= 0) {
            return normalized.replace(',', '.');
        }
        return normalized;
    }

    private static String rootMessage(Throwable throwable) {
        Throwable cursor = throwable;
        while (cursor.getCause() != null) {
            cursor = cursor.getCause();
        }
        String message = cursor.getMessage();
        return message == null || message.isBlank() ? cursor.toString() : message;
    }

    private static void log(Consumer<String> logger, String message) {
        if (logger != null) {
            logger.accept(message);
        }
    }

    @FunctionalInterface
    private interface RowBinder {
        void bind(Connection connection, PreparedStatement statement, Map<String, String> rowData, long rowNumber) throws Exception;
    }

    private static final class ImportState {
        private final String insertSql;
        private int pendingBatchRows;
        private long insertedRows;
        private long processedRows;

        private ImportState(String insertSql) {
            this.insertSql = insertSql;
        }
    }

    private record NameParts(String lastName, String firstName) {
    }

    private record ParsedSubjectGroup(String subjectCombinationCode, List<ParsedWeightedSubject> subjects) {
    }

    private record ParsedWeightedSubject(String subjectCode, int weight) {
    }

    private record SubjectFlags(
            boolean n1,
            boolean to,
            boolean li,
            boolean ho,
            boolean si,
            boolean va,
            boolean su,
            boolean di,
            boolean ti,
            boolean other,
            boolean ktpl
    ) {
    }
}
