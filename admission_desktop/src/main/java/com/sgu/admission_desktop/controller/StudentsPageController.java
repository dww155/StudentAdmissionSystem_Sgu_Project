package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantCreationRequest;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.Applicant.ListApplicantCreationRequest;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class StudentsPageController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<StudentRow> table;

    @FXML
    private TableColumn<StudentRow, String> colMaTs;

    @FXML
    private TableColumn<StudentRow, String> colHoTen;

    @FXML
    private TableColumn<StudentRow, String> colCccd;

    @FXML
    private TableColumn<StudentRow, String> colNgaySinh;

    @FXML
    private TableColumn<StudentRow, String> colGioiTinh;

    @FXML
    private TableColumn<StudentRow, String> colEmail;

    @FXML
    private TableColumn<StudentRow, String> colSdt;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;

    private final ObservableList<StudentRow> master = FXCollections.observableArrayList();
    private final ObservableList<StudentRow> filtered = FXCollections.observableArrayList();
    private final ApplicantService applicantService = new ApplicantService();
    private static final int IMPORT_BATCH_SIZE = 500;
    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.optional("registrationNumber", "Registration number", "registration", "ma ts", "mats", "so bao danh", "sbd"),
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD", "citizen id"),
            ExcelImportUtil.ColumnDefinition.optional("fullName", "Họ Tên", "ho ten", "full name", "ten thi sinh"),
            ExcelImportUtil.ColumnDefinition.optional("lastName", "Last name", "lastname", "ho lot", "ho"),
            ExcelImportUtil.ColumnDefinition.optional("firstName", "First name", "firstname", "ten"),
            ExcelImportUtil.ColumnDefinition.required("dateOfBirth", "Ngày sinh", "date of birth", "dob", "ngay sinh"),
            ExcelImportUtil.ColumnDefinition.optional("email", "Email"),
            ExcelImportUtil.ColumnDefinition.optional("phoneNumber", "Phone", "phone", "sdt", "so dien thoai"),
            ExcelImportUtil.ColumnDefinition.required("gender", "Giới tính", "gender", "gioi tinh"),
            ExcelImportUtil.ColumnDefinition.optional("applicantType", "ĐTƯT", "applicant type", "doi tuong", "dtut", "doi tuong uu tien"),
            ExcelImportUtil.ColumnDefinition.optional("region", "KVƯT", "region", "khu vuc", "kvut", "khu vuc uu tien"),
            ExcelImportUtil.ColumnDefinition.required("birthPlace", "Nơi sinh", "birth place", "noi sinh")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colCccd.setCellValueFactory(v -> v.getValue().cccdProperty());
        colNgaySinh.setCellValueFactory(v -> v.getValue().ngaySinhProperty());
        colGioiTinh.setCellValueFactory(v -> v.getValue().gioiTinhProperty());
        colEmail.setCellValueFactory(v -> v.getValue().emailProperty());
        colSdt.setCellValueFactory(v -> v.getValue().sdtProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(filtered);

        loadApplicants(0);

        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilter(newV));
    }

    @FXML
    private void onSearch() {
        applyFilter(searchField.getText());
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadApplicants(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadApplicants(currentPage + 1);
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add applicant",
                        List.of(
                                "Registration number",
                                "Last name",
                                "First name",
                                "CCCD",
                                "Date of birth (yyyy-MM-dd)",
                                "Email",
                                "Phone",
                                "Gender",
                                "Birth place",
                                "Applicant type",
                                "Region"
                        )
                )
                .ifPresent(this::createApplicant);
    }

    @FXML
    private void onImport() {
        var selectedFile = ExcelImportUtil.chooseExcelFile(
                table.getScene() == null ? null : table.getScene().getWindow(),
                "Import applicants"
        );
        if (selectedFile.isEmpty()) {
            return;
        }

        runApplicantImportInBackground(selectedFile.get());
    }

    private void runApplicantImportInBackground(Path filePath) {
        Task<ImportSummary> importTask = new Task<>() {
            @Override
            protected ImportSummary call() {
                List<ApplicantCreationRequest> requests = ExcelImportUtil.readRows(
                        filePath,
                        IMPORT_COLUMNS,
                        StudentsPageController.this::toImportedApplicantRequest
                );

                int importedCount = 0;
                for (int from = 0; from < requests.size(); from += IMPORT_BATCH_SIZE) {
                    int to = Math.min(from + IMPORT_BATCH_SIZE, requests.size());
                    List<ApplicantCreationRequest> batch = requests.subList(from, to);

                    applicantService.createBulk(
                            ListApplicantCreationRequest.builder()
                                    .applicantCreationRequestList(batch)
                                    .build()
                    );
                    importedCount += batch.size();
                }

                return new ImportSummary(requests.size(), importedCount);
            }
        };

        importTask.setOnSucceeded(event -> {
            ImportSummary summary = importTask.getValue();
            loadApplicants(currentPage);
            ControllerSupport.showInfo(
                    "Import applicants",
                    "Imported " + summary.importedCount() + "/" + summary.totalCount() + " applicants from Excel."
            );
        });

        importTask.setOnFailed(event -> {
            Throwable exception = importTask.getException();
            ControllerSupport.showError("Import applicants failed", ControllerSupport.extractMessage(exception));
        });

        Thread importThread = new Thread(importTask, "students-import-task");
        importThread.setDaemon(true);
        importThread.start();
    }

    private void loadApplicants(int requestedPage) {
        try {
            ApiResponse<Map<String, Object>> response = applicantService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<ApplicantResponse> applicants = extractApplicants(pageData);

            master.setAll(applicants.stream()
                    .map(this::toRow)
                    .toList());
            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, applicants.size(), "totalElements"), applicants.size());

            // Keep the current page index within available bounds if backend responses are inconsistent.
            if (currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }
            applyFilter(searchField == null ? "" : searchField.getText());
            updatePaginationControls();
        } catch (Exception e) {
            master.clear();
            filtered.clear();
            currentPage = 0;
            totalPages = 1;
            totalElements = 0;
            updatePaginationControls();
            ControllerSupport.showError("Load applicants failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createApplicant(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("registrationNumber", data.get("Registration number"));
            payload.put("lastName", data.get("Last name"));
            payload.put("firstName", data.get("First name"));
            payload.put("cccd", data.get("CCCD"));
            payload.put("dateOfBirth", ControllerSupport.parseDate(data.get("Date of birth (yyyy-MM-dd)"), "Date of birth"));
            payload.put("email", data.get("Email"));
            payload.put("phoneNumber", data.get("Phone"));
            payload.put("gender", normalizeGenderForApi(data.get("Gender"), "Gender"));
            payload.put("birthPlace", data.get("Birth place"));
            payload.put("applicantType", data.get("Applicant type"));
            payload.put("region", data.get("Region"));

            ApplicantCreationRequest request = ControllerSupport.convert(payload, ApplicantCreationRequest.class);
            applicantService.create(request);
            loadApplicants(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid applicant", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create applicant failed", ControllerSupport.extractMessage(e));
        }
    }

    private ApplicantCreationRequest toImportedApplicantRequest(Map<String, String> data) {
        String cccd = requireText(data.get("cccd"), "CCCD");
        NameParts nameParts = extractImportedName(data);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("registrationNumber", firstNonBlank(data.get("registrationNumber"), cccd, data.get("stt")));
        payload.put("lastName", nameParts.lastName());
        payload.put("firstName", nameParts.firstName());
        payload.put("cccd", cccd);
        payload.put("dateOfBirth", ControllerSupport.parseDate(data.get("dateOfBirth"), "Ngày sinh"));
        payload.put("email", defaultImportedEmail(data.get("email"), cccd));
        payload.put("phoneNumber", defaultImportedPhoneNumber(data.get("phoneNumber"), cccd));
        payload.put("gender", normalizeGenderForApi(data.get("gender"), "Giới tính"));
        payload.put("birthPlace", requireText(data.get("birthPlace"), "Nơi sinh"));
        payload.put("applicantType", defaultApplicantType(data.get("applicantType")));
        payload.put("region", defaultRegion(data.get("region")));
        return ControllerSupport.convert(payload, ApplicantCreationRequest.class);
    }

    private void applyFilter(String query) {
        String q = query == null ? "" : query.trim().toLowerCase(Locale.ROOT);
        filtered.setAll(master.filtered(r ->
                q.isEmpty()
                        || r.maTs().toLowerCase(Locale.ROOT).contains(q)
                        || r.hoTen().toLowerCase(Locale.ROOT).contains(q)
                        || r.cccd().toLowerCase(Locale.ROOT).contains(q)
        ));
    }

    private List<ApplicantResponse> extractApplicants(Map<String, Object> pageData) {
        Object content = firstNonNull(
                pageData.get("content"),
                pageData.get("items"),
                pageData.get("records")
        );
        if (content == null) {
            return List.of();
        }
        return ControllerSupport.convertList(
                content,
                new TypeReference<List<ApplicantResponse>>() {
                }
        );
    }

    private int extractInt(Map<String, Object> data, int defaultValue, String... keys) {
        for (String key : keys) {
            Integer parsed = parseInt(data.get(key));
            if (parsed != null) {
                return parsed;
            }
        }
        return defaultValue;
    }

    private long extractLong(Map<String, Object> data, long defaultValue, String... keys) {
        for (String key : keys) {
            Long parsed = parseLong(data.get(key));
            if (parsed != null) {
                return parsed;
            }
        }
        return defaultValue;
    }

    private Integer parseInt(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            String text = ControllerSupport.trimToNull(String.valueOf(value));
            return text == null ? null : Integer.parseInt(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Long parseLong(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        try {
            String text = ControllerSupport.trimToNull(String.valueOf(value));
            return text == null ? null : Long.parseLong(text);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private void updatePaginationControls() {
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " students");
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private StudentRow toRow(ApplicantResponse applicant) {
        Map<String, Object> data = ControllerSupport.toMap(applicant);
        String lastName = ControllerSupport.safeString(data.get("lastName"));
        String firstName = ControllerSupport.safeString(data.get("firstName"));
        String fullName = (lastName + " " + firstName).trim();

        return new StudentRow(
                ControllerSupport.safeString(data.get("registrationNumber")),
                fullName,
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.formatDateValue(data.get("dateOfBirth")),
                formatGenderForDisplay(data.get("gender")),
                ControllerSupport.safeString(data.get("email")),
                ControllerSupport.safeString(data.get("phoneNumber"))
        );
    }

    private NameParts extractImportedName(Map<String, String> data) {
        String fullName = ControllerSupport.trimToNull(data.get("fullName"));
        if (fullName != null) {
            return splitFullName(fullName);
        }

        String lastName = ControllerSupport.trimToNull(data.get("lastName"));
        String firstName = ControllerSupport.trimToNull(data.get("firstName"));
        if (lastName != null && firstName != null) {
            return new NameParts(lastName, firstName);
        }

        throw new IllegalArgumentException("Họ Tên or Last name/First name is required.");
    }

    private NameParts splitFullName(String fullName) {
        String normalized = fullName.trim().replaceAll("\\s+", " ");
        int lastSpace = normalized.lastIndexOf(' ');
        if (lastSpace < 0) {
            return new NameParts(normalized, normalized);
        }

        return new NameParts(
                normalized.substring(0, lastSpace).trim(),
                normalized.substring(lastSpace + 1).trim()
        );
    }

    private String defaultImportedEmail(String value, String cccd) {
        return firstNonBlank(value, cccd + "@import.local");
    }

    private String defaultImportedPhoneNumber(String value, String cccd) {
        return firstNonBlank(value, cccd);
    }

    private String defaultApplicantType(String value) {
        return firstNonBlank(value, "0");
    }

    private String defaultRegion(String value) {
        return firstNonBlank(value, "0");
    }

    private String normalizeGenderForApi(String value, String fieldName) {
        String normalized = normalizeGenderText(value, fieldName);
        if ("NAM".equals(normalized)) {
            return "NAM";
        }
        if ("NU".equals(normalized)) {
            return "NU";
        }
        throw new IllegalArgumentException(fieldName + " must be Nam or N\u1EEF.");
    }

    private String formatGenderForDisplay(Object value) {
        String raw = ControllerSupport.safeString(value);
        if (raw.isBlank()) {
            return "";
        }

        try {
            String normalized = normalizeGenderText(raw, "Gender");
            if ("NAM".equals(normalized)) {
                return "Nam";
            }
            if ("NU".equals(normalized)) {
                return "N\u1EEF";
            }
        } catch (IllegalArgumentException ignored) {
            // Keep original value if backend has an unexpected code.
        }

        return raw;
    }

    private String normalizeGenderText(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String normalized = Normalizer.normalize(trimmed, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT)
                .trim();

        return normalized;
    }

    private String requireText(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = ControllerSupport.trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }

    private record NameParts(String lastName, String firstName) {
    }

    private record ImportSummary(int totalCount, int importedCount) {
    }
}
