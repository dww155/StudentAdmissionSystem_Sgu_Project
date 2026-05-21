package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.VsatResult.ListVsatResultCreationRequest;
import com.sgu.admission_desktop.dto.VsatResult.VsatResultCreationRequest;
import com.sgu.admission_desktop.dto.VsatResult.VsatResultResponse;
import com.sgu.admission_desktop.service.VsatResultService;
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

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class VsatResultsPageController implements Initializable {

    @FXML
    private TableView<VsatResultRow> table;

    @FXML
    private TableColumn<VsatResultRow, String> colCccd;

    @FXML
    private TableColumn<VsatResultRow, String> colDotThi;

    @FXML
    private TableColumn<VsatResultRow, String> colMaDotThi;

    @FXML
    private TableColumn<VsatResultRow, String> colNgayThi;

    @FXML
    private TableColumn<VsatResultRow, String> colMonThi;

    @FXML
    private TableColumn<VsatResultRow, String> colDiem;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;
    @FXML
    private TextField searchField;

    private final ObservableList<VsatResultRow> items = FXCollections.observableArrayList();
    private final VsatResultService vsatResultService = new VsatResultService();

    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("dotThi", "Dot thi"),
            ExcelImportUtil.ColumnDefinition.required("maDotThi", "Ma dot thi"),
            ExcelImportUtil.ColumnDefinition.required("ngayThi", "Ngay thi", "date"),
            ExcelImportUtil.ColumnDefinition.required("namThi", "Nam thi"),
            ExcelImportUtil.ColumnDefinition.required("maMonThi", "Ma mon thi"),
            ExcelImportUtil.ColumnDefinition.required("tenMonThi", "Ten mon thi"),
            ExcelImportUtil.ColumnDefinition.required("diem", "Diem"),
            ExcelImportUtil.ColumnDefinition.required("thangDiem", "Thang diem"),
            ExcelImportUtil.ColumnDefinition.required("maDvtctdl", "Ma dvtctdl"),
            ExcelImportUtil.ColumnDefinition.required("tenDvtctdl", "Ten dvtctdl")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colCccd.setCellValueFactory(v -> v.getValue().cccdProperty());
        colDotThi.setCellValueFactory(v -> v.getValue().dotThiProperty());
        colMaDotThi.setCellValueFactory(v -> v.getValue().maDotThiProperty());
        colNgayThi.setCellValueFactory(v -> v.getValue().ngayThiProperty());
        colMonThi.setCellValueFactory(v -> v.getValue().monThiProperty());
        colDiem.setCellValueFactory(v -> v.getValue().diemProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadVsatResults(0);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadVsatResults(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadVsatResults(currentPage + 1);
    }

    @FXML
    private void onSearchByCccd() {
        String cccd = ControllerSupport.trimToNull(searchField == null ? null : searchField.getText());
        if (cccd == null) {
            loadVsatResults(0);
            return;
        }

        try {
            ApiResponse<List<VsatResultResponse>> response = vsatResultService.getByCccd(cccd);
            List<VsatResultResponse> vsatResults = response.getData() == null ? List.of() : response.getData();
            if (vsatResults.isEmpty()) {
                items.clear();
                currentPage = 0;
                totalPages = 1;
                totalElements = 0;
                updatePaginationControls();
                return;
            }

            items.setAll(vsatResults.stream().map(this::toRow).toList());
            currentPage = 0;
            totalPages = 1;
            totalElements = vsatResults.size();
            updatePaginationControls();
        } catch (Exception e) {
            ControllerSupport.showError("Search VSAT by CCCD failed", ControllerSupport.extractMessage(e));
        }
    }

    private void loadVsatResults(int requestedPage) {
        try {
            ApiResponse<Map<String, Object>> response = vsatResultService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<VsatResultResponse> vsatResults = extractVsatResults(pageData);

            items.setAll(vsatResults.stream().map(this::toRow).toList());

            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, vsatResults.size(), "totalElements"), vsatResults.size());

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
            ControllerSupport.showError("Load VSAT results failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add VSAT result",
                        List.of(
                                "CCCD",
                                "Dot thi",
                                "Ma dot thi",
                                "Ngay thi (yyyy-MM-dd)",
                                "Nam thi",
                                "Ma mon thi",
                                "Ten mon thi",
                                "Diem",
                                "Thang diem",
                                "Ma dvtctdl",
                                "Ten dvtctdl"
                        )
                )
                .ifPresent(this::createVsatResult);
    }

    @FXML
    private void onImport() {
        try {
            var importedVsatResults = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import VSAT results",
                    IMPORT_COLUMNS,
                    this::toImportedVsatResultRequest
            );

            if (importedVsatResults.isEmpty()) {
                return;
            }

            List<VsatResultCreationRequest> requests = importedVsatResults.get();
            vsatResultService.createBulk(
                    ListVsatResultCreationRequest.builder()
                            .vsatResultCreationRequestList(requests)
                            .build()
            );
            loadVsatResults(currentPage);
            ControllerSupport.showInfo("Import VSAT results", "Imported " + requests.size() + " VSAT records from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import VSAT results failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import VSAT results failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createVsatResult(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("dotThi", ControllerSupport.parseInt(data.get("Dot thi"), "Dot thi"));
            payload.put("maDotThi", data.get("Ma dot thi"));
            payload.put("ngayThi", ControllerSupport.parseDate(data.get("Ngay thi (yyyy-MM-dd)"), "Ngay thi"));
            payload.put("namThi", ControllerSupport.parseInt(data.get("Nam thi"), "Nam thi"));
            payload.put("maMonThi", data.get("Ma mon thi"));
            payload.put("tenMonThi", data.get("Ten mon thi"));
            payload.put("diem", parseDouble(data.get("Diem"), "Diem"));
            payload.put("thangDiem", ControllerSupport.parseInt(data.get("Thang diem"), "Thang diem"));
            payload.put("maDvtctdl", data.get("Ma dvtctdl"));
            payload.put("tenDvtctdl", data.get("Ten dvtctdl"));

            VsatResultCreationRequest request = ControllerSupport.convert(payload, VsatResultCreationRequest.class);
            vsatResultService.create(request);
            loadVsatResults(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid VSAT result", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create VSAT result failed", ControllerSupport.extractMessage(e));
        }
    }

    private VsatResultCreationRequest toImportedVsatResultRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", data.get("cccd"));
        payload.put("dotThi", ControllerSupport.parseInt(data.get("dotThi"), "Dot thi"));
        payload.put("maDotThi", data.get("maDotThi"));
        payload.put("ngayThi", ControllerSupport.parseDate(data.get("ngayThi"), "Ngay thi"));
        payload.put("namThi", ControllerSupport.parseInt(data.get("namThi"), "Nam thi"));
        payload.put("maMonThi", data.get("maMonThi"));
        payload.put("tenMonThi", data.get("tenMonThi"));
        payload.put("diem", parseDouble(data.get("diem"), "Diem"));
        payload.put("thangDiem", ControllerSupport.parseInt(data.get("thangDiem"), "Thang diem"));
        payload.put("maDvtctdl", data.get("maDvtctdl"));
        payload.put("tenDvtctdl", data.get("tenDvtctdl"));
        return ControllerSupport.convert(payload, VsatResultCreationRequest.class);
    }

    private List<VsatResultResponse> extractVsatResults(Map<String, Object> pageData) {
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
                new TypeReference<List<VsatResultResponse>>() {
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
                    "Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " VSAT records"
            );
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private VsatResultRow toRow(VsatResultResponse vsatResult) {
        Map<String, Object> data = ControllerSupport.toMap(vsatResult);
        String maMon = ControllerSupport.safeString(data.get("maMonThi"));
        String tenMon = ControllerSupport.safeString(data.get("tenMonThi"));
        String monThi = maMon.isBlank() ? tenMon : maMon + " - " + tenMon;

        return new VsatResultRow(
                ControllerSupport.safeString(data.get("cccd")),
                ControllerSupport.safeString(data.get("dotThi")),
                ControllerSupport.safeString(data.get("maDotThi")),
                ControllerSupport.formatDateValue(data.get("ngayThi")),
                monThi,
                formatScore(data.get("diem"), data.get("thangDiem"))
        );
    }

    private String formatScore(Object diem, Object thangDiem) {
        String score = ControllerSupport.safeString(diem);
        String scale = ControllerSupport.safeString(thangDiem);
        if (score.isBlank()) {
            return "";
        }
        return scale.isBlank() ? score : score + "/" + scale;
    }

    private double parseDouble(String value, String fieldName) {
        return ControllerSupport.parseDecimal(value, fieldName).doubleValue();
    }
}
