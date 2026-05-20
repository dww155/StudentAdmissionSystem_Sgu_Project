package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.ListPriorityBonusPointCreationRequest;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointCreationRequest;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointResponse;
import com.sgu.admission_desktop.dto.PriorityBonusPoint.PriorityBonusPointUpdateRequest;
import com.sgu.admission_desktop.service.PriorityBonusPointService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;

public class PriorityBonusPointsPageController implements Initializable {

    @FXML
    private TextField searchField;

    @FXML
    private TableView<PriorityBonusPointRow> table;

    @FXML
    private TableColumn<PriorityBonusPointRow, String> colCccd;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colLevel;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colTeam;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colSubjectCode;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colPrizeType;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colBonusPointForSubject;
    @FXML
    private TableColumn<PriorityBonusPointRow, String> colBonusPointForSubjectGroup;

    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private Label pageInfoLabel;

    private final ObservableList<PriorityBonusPointRow> items = FXCollections.observableArrayList();
    private final PriorityBonusPointService priorityBonusPointService = new PriorityBonusPointService();

    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.optional("level", "Level"),
            ExcelImportUtil.ColumnDefinition.optional("team", "Team"),
            ExcelImportUtil.ColumnDefinition.optional("subjectCode", "Subject code"),
            ExcelImportUtil.ColumnDefinition.optional("prizeType", "Prize type"),
            ExcelImportUtil.ColumnDefinition.required("bonusPointForSubject", "Bonus point for subject"),
            ExcelImportUtil.ColumnDefinition.required("bonusPointForSubjectGroup", "Bonus point for subject group")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCccd.setCellValueFactory(v -> v.getValue().cccdProperty());
        colLevel.setCellValueFactory(v -> v.getValue().levelProperty());
        colTeam.setCellValueFactory(v -> v.getValue().teamProperty());
        colSubjectCode.setCellValueFactory(v -> v.getValue().subjectCodeProperty());
        colPrizeType.setCellValueFactory(v -> v.getValue().prizeTypeProperty());
        colBonusPointForSubject.setCellValueFactory(v -> v.getValue().bonusPointForSubjectProperty());
        colBonusPointForSubjectGroup.setCellValueFactory(v -> v.getValue().bonusPointForSubjectGroupProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadPriorityBonusPoints(0);
    }

    @FXML
    private void onSearchByCccd() {
        String cccd = ControllerSupport.trimToNull(searchField == null ? null : searchField.getText());
        if (cccd == null) {
            loadPriorityBonusPoints(0);
            return;
        }

        try {
            ApiResponse<PriorityBonusPointResponse> response = priorityBonusPointService.getByCccd(cccd);
            PriorityBonusPointResponse point = response.getData();
            if (point == null) {
                items.clear();
                currentPage = 0;
                totalPages = 1;
                totalElements = 0;
                updatePaginationControls();
                return;
            }

            items.setAll(toRow(point));
            currentPage = 0;
            totalPages = 1;
            totalElements = 1;
            updatePaginationControls();
        } catch (Exception e) {
            ControllerSupport.showError("Search priority bonus point failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadPriorityBonusPoints(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadPriorityBonusPoints(currentPage + 1);
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add priority bonus point",
                        List.of(
                                "CCCD",
                                "Level (optional)",
                                "Team (optional)",
                                "Subject code (optional)",
                                "Prize type (optional)",
                                "Bonus point for subject",
                                "Bonus point for subject group"
                        )
                )
                .ifPresent(this::createPriorityBonusPoint);
    }

    @FXML
    private void onUpdateSelected() {
        PriorityBonusPointRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ControllerSupport.showInfo("Update priority bonus point", "Please select one record in table.");
            return;
        }

        CreateRowPopup.show(
                        "Update priority bonus point",
                        List.of(
                                "Level (optional)",
                                "Team (optional)",
                                "Subject code (optional)",
                                "Prize type (optional)",
                                "Bonus point for subject",
                                "Bonus point for subject group"
                        )
                )
                .ifPresent(data -> updatePriorityBonusPoint(selected.id(), data));
    }

    @FXML
    private void onDeleteSelected() {
        PriorityBonusPointRow selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            ControllerSupport.showInfo("Delete priority bonus point", "Please select one record in table.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete priority bonus point");
        alert.setHeaderText(null);
        alert.setContentText("Delete selected record for CCCD " + selected.cccdProperty().get() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) {
            return;
        }

        try {
            priorityBonusPointService.delete(selected.id());
            loadPriorityBonusPoints(currentPage);
        } catch (Exception e) {
            ControllerSupport.showError("Delete priority bonus point failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onImport() {
        try {
            var importedPoints = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import priority bonus points",
                    IMPORT_COLUMNS,
                    this::toImportedRequest
            );

            if (importedPoints.isEmpty()) {
                return;
            }

            List<PriorityBonusPointCreationRequest> requests = importedPoints.get();
            priorityBonusPointService.createBulk(
                    ListPriorityBonusPointCreationRequest.builder()
                            .priorityBonusPointCreationRequestList(requests)
                            .build()
            );
            loadPriorityBonusPoints(currentPage);
            ControllerSupport.showInfo(
                    "Import priority bonus points",
                    "Imported " + requests.size() + " records from Excel."
            );
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import priority bonus points failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import priority bonus points failed", ControllerSupport.extractMessage(e));
        }
    }

    private void loadPriorityBonusPoints(int requestedPage) {
        try {
            ApiResponse<Map<String, Object>> response = priorityBonusPointService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<PriorityBonusPointResponse> points = extractItems(pageData);

            items.setAll(points.stream().map(this::toRow).toList());
            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, points.size(), "totalElements"), points.size());

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
            ControllerSupport.showError("Load priority bonus points failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createPriorityBonusPoint(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("level", blankToNull(data.get("Level (optional)")));
            payload.put("team", blankToNull(data.get("Team (optional)")));
            payload.put("subjectCode", blankToNull(data.get("Subject code (optional)")));
            payload.put("prizeType", blankToNull(data.get("Prize type (optional)")));
            payload.put("bonusPointForSubject", ControllerSupport.parseDecimal(data.get("Bonus point for subject"), "Bonus point for subject"));
            payload.put("bonusPointForSubjectGroup", ControllerSupport.parseDecimal(data.get("Bonus point for subject group"), "Bonus point for subject group"));

            PriorityBonusPointCreationRequest request = ControllerSupport.convert(payload, PriorityBonusPointCreationRequest.class);
            priorityBonusPointService.create(request);
            loadPriorityBonusPoints(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid priority bonus point", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create priority bonus point failed", ControllerSupport.extractMessage(e));
        }
    }

    private void updatePriorityBonusPoint(int id, Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("level", blankToNull(data.get("Level (optional)")));
            payload.put("team", blankToNull(data.get("Team (optional)")));
            payload.put("subjectCode", blankToNull(data.get("Subject code (optional)")));
            payload.put("prizeType", blankToNull(data.get("Prize type (optional)")));
            payload.put("bonusPointForSubject", ControllerSupport.parseDecimal(data.get("Bonus point for subject"), "Bonus point for subject"));
            payload.put("bonusPointForSubjectGroup", ControllerSupport.parseDecimal(data.get("Bonus point for subject group"), "Bonus point for subject group"));

            PriorityBonusPointUpdateRequest request = ControllerSupport.convert(payload, PriorityBonusPointUpdateRequest.class);
            priorityBonusPointService.update(id, request);
            loadPriorityBonusPoints(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid priority bonus point", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Update priority bonus point failed", ControllerSupport.extractMessage(e));
        }
    }

    private PriorityBonusPointCreationRequest toImportedRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", data.get("cccd"));
        payload.put("level", blankToNull(data.get("level")));
        payload.put("team", blankToNull(data.get("team")));
        payload.put("subjectCode", blankToNull(data.get("subjectCode")));
        payload.put("prizeType", blankToNull(data.get("prizeType")));
        payload.put("bonusPointForSubject", ControllerSupport.parseDecimal(data.get("bonusPointForSubject"), "Bonus point for subject"));
        payload.put("bonusPointForSubjectGroup", ControllerSupport.parseDecimal(data.get("bonusPointForSubjectGroup"), "Bonus point for subject group"));
        return ControllerSupport.convert(payload, PriorityBonusPointCreationRequest.class);
    }

    private List<PriorityBonusPointResponse> extractItems(Map<String, Object> pageData) {
        Object content = firstNonNull(pageData.get("content"), pageData.get("items"), pageData.get("records"));
        if (content == null) {
            return List.of();
        }
        return ControllerSupport.convertList(content, new TypeReference<List<PriorityBonusPointResponse>>() {
        });
    }

    private PriorityBonusPointRow toRow(PriorityBonusPointResponse point) {
        Map<String, Object> data = ControllerSupport.toMap(point);
        Integer id = parseInt(data.get("id"));
        return new PriorityBonusPointRow(
                id,
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.safeString(data.get("level")),
                ControllerSupport.safeString(data.get("team")),
                ControllerSupport.safeString(data.get("subjectCode")),
                ControllerSupport.safeString(data.get("prizeType")),
                ControllerSupport.safeString(data.get("bonusPointForSubject")),
                ControllerSupport.safeString(data.get("bonusPointForSubjectGroup"))
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
            pageInfoLabel.setText("Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " priority bonus records");
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private String blankToNull(String value) {
        return ControllerSupport.trimToNull(value);
    }
}
