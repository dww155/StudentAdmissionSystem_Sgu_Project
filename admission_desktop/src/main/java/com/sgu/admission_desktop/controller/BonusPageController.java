package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.ListAdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.service.AdmissionBonusScoreService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class BonusPageController implements Initializable {

    @FXML
    private TableView<BonusRow> table;

    @FXML
    private TableColumn<BonusRow, String> colMaTs;

    @FXML
    private TableColumn<BonusRow, String> colDiemCong;

    @FXML
    private TableColumn<BonusRow, String> colLyDo;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;

    private final ObservableList<BonusRow> items = FXCollections.observableArrayList();
    private final AdmissionBonusScoreService admissionBonusScoreService = new AdmissionBonusScoreService();

    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colDiemCong.setCellValueFactory(v -> v.getValue().diemCongProperty());
        colLyDo.setCellValueFactory(v -> v.getValue().lyDoProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadBonusScores(0);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadBonusScores(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadBonusScores(currentPage + 1);
    }

    private void loadBonusScores(int requestedPage) {
        try {
            ApiResponse<Map<String, Object>> response = admissionBonusScoreService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<AdmissionBonusScoreResponse> bonusScores = extractBonusScores(pageData);

            items.setAll(bonusScores.stream()
                    .map(this::toRow)
                    .toList());

            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, bonusScores.size(), "totalElements"), bonusScores.size());

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
            ControllerSupport.showError("Load bonus scores failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add bonus score",
                        List.of(
                                "CCCD",
                                "Major code",
                                "Subject combination code",
                                "Method",
                                "Bonus score",
                                "Priority score",
                                "Total score",
                                "DC keys",
                                "Note (optional)"
                        )
                )
                .ifPresent(this::createBonusScore);
    }

    @FXML
    private void onImport() {
        try {
            var importedBonusScores = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import bonus scores",
                    IMPORT_COLUMNS,
                    this::toImportedBonusScoreRequest
            );

            if (importedBonusScores.isEmpty()) {
                return;
            }

            List<AdmissionBonusScoreCreationRequest> requests = importedBonusScores.get();
            admissionBonusScoreService.createBulk(
                    ListAdmissionBonusScoreCreationRequest.builder()
                            .admissionBonusScoreCreationRequestList(requests)
                            .build()
            );
            loadBonusScores(currentPage);
            ControllerSupport.showInfo(
                    "Import bonus scores",
                    "Imported " + requests.size() + " bonus scores from Excel."
            );
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import bonus scores failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import bonus scores failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createBonusScore(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("majorCode", data.get("Major code"));
            payload.put("subjectCombinationCode", data.get("Subject combination code"));
            payload.put("method", data.get("Method"));
            payload.put("bonusScore", ControllerSupport.parseDecimal(data.get("Bonus score"), "Bonus score"));
            payload.put("priorityScore", ControllerSupport.parseDecimal(data.get("Priority score"), "Priority score"));
            payload.put("totalScore", ControllerSupport.parseDecimal(data.get("Total score"), "Total score"));
            payload.put("dcKeys", data.get("DC keys"));
            payload.put("note", blankToNull(data.get("Note (optional)")));

            AdmissionBonusScoreCreationRequest request = ControllerSupport.convert(payload, AdmissionBonusScoreCreationRequest.class);
            admissionBonusScoreService.create(request);
            loadBonusScores(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid bonus score", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create bonus score failed", ControllerSupport.extractMessage(e));
        }
    }

    private AdmissionBonusScoreCreationRequest toImportedBonusScoreRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", data.get("cccd"));
        payload.put("majorCode", data.get("majorCode"));
        payload.put("subjectCombinationCode", data.get("subjectCombinationCode"));
        payload.put("method", data.get("method"));
        payload.put("bonusScore", ControllerSupport.parseDecimal(data.get("bonusScore"), "Bonus score"));
        payload.put("priorityScore", ControllerSupport.parseDecimal(data.get("priorityScore"), "Priority score"));
        payload.put("totalScore", ControllerSupport.parseDecimal(data.get("totalScore"), "Total score"));
        payload.put("dcKeys", data.get("dcKeys"));
        payload.put("note", blankToNull(data.get("note")));
        return ControllerSupport.convert(payload, AdmissionBonusScoreCreationRequest.class);
    }

    private List<AdmissionBonusScoreResponse> extractBonusScores(Map<String, Object> pageData) {
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
                new TypeReference<List<AdmissionBonusScoreResponse>>() {
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
            pageInfoLabel.setText("Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " bonus records");
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private BonusRow toRow(AdmissionBonusScoreResponse bonusScore) {
        Map<String, Object> data = ControllerSupport.toMap(bonusScore);
        return new BonusRow(
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.safeString(data.get("bonusScore")),
                ControllerSupport.safeString(data.get("note"))
        );
    }

    private String blankToNull(String value) {
        return ControllerSupport.trimToNull(value);
    }
}
