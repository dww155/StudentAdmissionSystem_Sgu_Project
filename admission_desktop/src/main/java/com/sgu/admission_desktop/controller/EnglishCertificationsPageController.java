package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.EnglishCertification.EnglishCertificationCreationRequest;
import com.sgu.admission_desktop.dto.EnglishCertification.EnglishCertificationResponse;
import com.sgu.admission_desktop.dto.EnglishCertification.ListEnglishCertificationCreationRequest;
import com.sgu.admission_desktop.service.EnglishCertificationService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class EnglishCertificationsPageController implements Initializable {

    @FXML
    private TableView<EnglishCertificationRow> table;

    @FXML
    private TableColumn<EnglishCertificationRow, String> colCccd;

    @FXML
    private TableColumn<EnglishCertificationRow, String> colCertificationName;

    @FXML
    private TableColumn<EnglishCertificationRow, String> colCertificationScore;

    @FXML
    private TableColumn<EnglishCertificationRow, String> colConversionScore;

    @FXML
    private TableColumn<EnglishCertificationRow, String> colBonusScore;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;
    @FXML
    private TextField searchField;

    private final ObservableList<EnglishCertificationRow> items = FXCollections.observableArrayList();
    private final EnglishCertificationService englishCertificationService = new EnglishCertificationService();

    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("certificationName", "Certification name", "ten chung chi"),
            ExcelImportUtil.ColumnDefinition.required("certificationScore", "Certification score", "diem chung chi"),
            ExcelImportUtil.ColumnDefinition.optional("conversionScore", "Conversion score", "diem quy doi"),
            ExcelImportUtil.ColumnDefinition.optional("bonusScore", "Bonus score", "diem cong")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCccd.setCellValueFactory(v -> v.getValue().cccdProperty());
        colCertificationName.setCellValueFactory(v -> v.getValue().certificationNameProperty());
        colCertificationScore.setCellValueFactory(v -> v.getValue().certificationScoreProperty());
        colConversionScore.setCellValueFactory(v -> v.getValue().conversionScoreProperty());
        colBonusScore.setCellValueFactory(v -> v.getValue().bonusScoreProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadEnglishCertifications(0);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadEnglishCertifications(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadEnglishCertifications(currentPage + 1);
    }

    @FXML
    private void onSearchByCccd() {
        String cccd = ControllerSupport.trimToNull(searchField == null ? null : searchField.getText());
        if (cccd == null) {
            loadEnglishCertifications(0);
            return;
        }

        try {
            ApiResponse<EnglishCertificationResponse> response = englishCertificationService.getByCccd(cccd);
            EnglishCertificationResponse certification = response.getData();
            if (certification == null) {
                items.clear();
                currentPage = 0;
                totalPages = 1;
                totalElements = 0;
                updatePaginationControls();
                return;
            }

            items.setAll(toRow(certification));
            currentPage = 0;
            totalPages = 1;
            totalElements = 1;
            updatePaginationControls();
        } catch (Exception e) {
            ControllerSupport.showError("Search English certification by CCCD failed", ControllerSupport.extractMessage(e));
        }
    }

    private void loadEnglishCertifications(int requestedPage) {
        try {
            ApiResponse<Map<String, Object>> response = englishCertificationService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<EnglishCertificationResponse> certifications = extractEnglishCertifications(pageData);

            items.setAll(certifications.stream().map(this::toRow).toList());

            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, certifications.size(), "totalElements"), certifications.size());

            if (currentPage >= totalPages) {
                currentPage = totalPages - 1;
            }

            updatePaginationControls();
        } catch (Exception e) {
            items.clear();
            currentPage = 0;
            totalPages = 1;
            totalElements = 0;
            updatePaginationControls();
            ControllerSupport.showError("Load English certifications failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add English certification",
                        List.of(
                                "CCCD",
                                "Certification name",
                                "Certification score",
                                "Conversion score (optional)",
                                "Bonus score (optional)"
                        )
                )
                .ifPresent(this::createEnglishCertification);
    }

    @FXML
    private void onImport() {
        try {
            var importedCertifications = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import English certifications",
                    IMPORT_COLUMNS,
                    this::toImportedEnglishCertificationRequest
            );

            if (importedCertifications.isEmpty()) {
                return;
            }

            List<EnglishCertificationCreationRequest> requests = importedCertifications.get();
            englishCertificationService.createBulk(
                    ListEnglishCertificationCreationRequest.builder()
                            .englishCertificationCreationRequestList(requests)
                            .build()
            );
            loadEnglishCertifications(currentPage);
            ControllerSupport.showInfo(
                    "Import English certifications",
                    "Imported " + requests.size() + " English certifications from Excel."
            );
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import English certifications failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import English certifications failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createEnglishCertification(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("certificationName", data.get("Certification name"));
            payload.put("certificationScore", ControllerSupport.parseDecimal(data.get("Certification score"), "Certification score"));
            payload.put("conversionScore", parseOptionalDecimal(data.get("Conversion score (optional)"), "Conversion score"));
            payload.put("bonusScore", parseOptionalDecimal(data.get("Bonus score (optional)"), "Bonus score"));

            EnglishCertificationCreationRequest request = ControllerSupport.convert(payload, EnglishCertificationCreationRequest.class);
            englishCertificationService.create(request);
            loadEnglishCertifications(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid English certification", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create English certification failed", ControllerSupport.extractMessage(e));
        }
    }

    private EnglishCertificationCreationRequest toImportedEnglishCertificationRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", data.get("cccd"));
        payload.put("certificationName", data.get("certificationName"));
        payload.put("certificationScore", ControllerSupport.parseDecimal(data.get("certificationScore"), "Certification score"));
        payload.put("conversionScore", parseOptionalDecimal(data.get("conversionScore"), "Conversion score"));
        payload.put("bonusScore", parseOptionalDecimal(data.get("bonusScore"), "Bonus score"));
        return ControllerSupport.convert(payload, EnglishCertificationCreationRequest.class);
    }

    private List<EnglishCertificationResponse> extractEnglishCertifications(Map<String, Object> pageData) {
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
                new TypeReference<List<EnglishCertificationResponse>>() {
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
            pageInfoLabel.setText(
                    "Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " certificates"
            );
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private EnglishCertificationRow toRow(EnglishCertificationResponse certification) {
        Map<String, Object> data = ControllerSupport.toMap(certification);
        return new EnglishCertificationRow(
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.safeString(data.get("certificationName")),
                formatNumber(data.get("certificationScore")),
                formatNumber(data.get("conversionScore")),
                formatNumber(data.get("bonusScore"))
        );
    }

    private BigDecimal parseOptionalDecimal(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            return null;
        }
        return ControllerSupport.parseDecimal(trimmed, fieldName);
    }

    private String formatNumber(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof BigDecimal bigDecimal) {
            return ControllerSupport.formatDecimal(bigDecimal);
        }
        try {
            return ControllerSupport.formatDecimal(new BigDecimal(String.valueOf(value)));
        } catch (RuntimeException ignored) {
            return ControllerSupport.safeString(value);
        }
    }
}
