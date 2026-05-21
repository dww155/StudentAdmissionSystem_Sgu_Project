package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.admission_desktop.dto.AdmissionPreference.ListAdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.service.AdmissionPreferenceService;
import com.sgu.admission_desktop.service.MajorService;
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
import java.util.stream.Collectors;

public class WishesPageController implements Initializable {

    @FXML
    private TableView<WishRow> table;

    @FXML
    private TableColumn<WishRow, String> colMaTs;

    @FXML
    private TableColumn<WishRow, String> colNguyenVong;

    @FXML
    private TableColumn<WishRow, String> colTenNganh;

    @FXML
    private TableColumn<WishRow, String> colMaToHop;

    @FXML
    private TableColumn<WishRow, String> colTongDiem;

    @FXML
    private TableColumn<WishRow, String> colTrangThai;

    @FXML
    private Button prevPageButton;

    @FXML
    private Button nextPageButton;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private TextField searchField;

    private final ObservableList<WishRow> items = FXCollections.observableArrayList();
    private final AdmissionPreferenceService admissionPreferenceService = new AdmissionPreferenceService();
    private final MajorService majorService = new MajorService();

    private static final int PAGE_SIZE = 20;
    private static final String PAGE_SORT_BY = "id";
    private static final String PAGE_SORT_DIR = "asc";

    private int currentPage = 0;
    private int totalPages = 1;
    private long totalElements = 0;

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "Major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.required("priorityOrder", "Priority order", "nguyen vong", "thu tu nguyen vong"),
            ExcelImportUtil.ColumnDefinition.required("nvKeys", "NV keys", "nv key"),
            ExcelImportUtil.ColumnDefinition.optional("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.optional("subjectGroup", "Subject group", "to hop", "subject combination")
    );

    private Map<String, String> majorNameByCode = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colNguyenVong.setCellValueFactory(v -> v.getValue().nguyenVongProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTongDiem.setCellValueFactory(v -> v.getValue().tongDiemProperty());
        colTrangThai.setCellValueFactory(v -> v.getValue().trangThaiProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadWishes(0);
    }

    @FXML
    private void onPreviousPage() {
        if (currentPage <= 0) {
            return;
        }
        loadWishes(currentPage - 1);
    }

    @FXML
    private void onNextPage() {
        if (currentPage + 1 >= totalPages) {
            return;
        }
        loadWishes(currentPage + 1);
    }

    @FXML
    private void onSearchByCccd() {
        String cccd = ControllerSupport.trimToNull(searchField == null ? null : searchField.getText());
        if (cccd == null) {
            loadWishes(0);
            return;
        }

        try {
            majorNameByCode = loadMajorNames();
            ApiResponse<List<AdmissionPreferenceResponse>> response = admissionPreferenceService.getAll();
            List<AdmissionPreferenceResponse> wishes = response.getData() == null ? List.of() : response.getData();

            List<AdmissionPreferenceResponse> filtered = wishes.stream()
                    .filter(wish -> cccd.equals(ControllerSupport.safeString(ControllerSupport.toMap(wish).get("cccd"))))
                    .toList();

            items.setAll(filtered.stream().map(this::toRow).toList());
            currentPage = 0;
            totalPages = 1;
            totalElements = filtered.size();
            updatePaginationControls();
        } catch (Exception e) {
            ControllerSupport.showError("Search wishes by CCCD failed", ControllerSupport.extractMessage(e));
        }
    }

    private void loadWishes(int requestedPage) {
        try {
            majorNameByCode = loadMajorNames();

            ApiResponse<Map<String, Object>> response = admissionPreferenceService.getPaginated(
                    Math.max(requestedPage, 0),
                    PAGE_SIZE,
                    PAGE_SORT_BY,
                    PAGE_SORT_DIR
            );

            Map<String, Object> pageData = response.getData() == null ? Map.of() : response.getData();
            List<AdmissionPreferenceResponse> wishes = extractWishes(pageData);

            items.setAll(wishes.stream()
                    .map(this::toRow)
                    .toList());

            currentPage = Math.max(extractInt(pageData, requestedPage, "pageNumber", "number", "page"), 0);
            totalPages = Math.max(extractInt(pageData, 1, "totalPages"), 1);
            totalElements = Math.max(extractLong(pageData, wishes.size(), "totalElements"), wishes.size());

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
            ControllerSupport.showError("Load wishes failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add wish",
                        List.of(
                                "CCCD",
                                "Major code",
                                "Priority order",
                                "NV keys",
                                "Method (optional)",
                                "Subject group (optional)"
                        )
                )
                .ifPresent(this::createWish);
    }

    @FXML
    private void onImport() {
        try {
            var importedWishes = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import wishes",
                    IMPORT_COLUMNS,
                    this::toImportedWishRequest
            );

            if (importedWishes.isEmpty()) {
                return;
            }

            List<AdmissionPreferenceCreationRequest> requests = importedWishes.get();
            admissionPreferenceService.createBulk(
                    ListAdmissionPreferenceCreationRequest.builder()
                            .admissionPreferenceCreationRequestList(requests)
                            .build()
            );
            loadWishes(currentPage);
            ControllerSupport.showInfo("Import wishes", "Imported " + requests.size() + " wishes from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import wishes failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import wishes failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createWish(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("majorCode", data.get("Major code"));
            payload.put("priorityOrder", ControllerSupport.parseInt(data.get("Priority order"), "Priority order"));
            payload.put("nvKeys", data.get("NV keys"));
            payload.put("method", blankToNull(data.get("Method (optional)")));
            payload.put("subjectGroup", blankToNull(data.get("Subject group (optional)")));

            AdmissionPreferenceCreationRequest request = ControllerSupport.convert(payload, AdmissionPreferenceCreationRequest.class);
            admissionPreferenceService.create(request);
            loadWishes(currentPage);
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid wish", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create wish failed", ControllerSupport.extractMessage(e));
        }
    }

    private AdmissionPreferenceCreationRequest toImportedWishRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", data.get("cccd"));
        payload.put("majorCode", data.get("majorCode"));
        payload.put("priorityOrder", ControllerSupport.parseInt(data.get("priorityOrder"), "Priority order"));
        payload.put("nvKeys", data.get("nvKeys"));
        payload.put("method", blankToNull(data.get("method")));
        payload.put("subjectGroup", blankToNull(data.get("subjectGroup")));
        return ControllerSupport.convert(payload, AdmissionPreferenceCreationRequest.class);
    }

    private Map<String, String> loadMajorNames() {
        ApiResponse<List<MajorResponse>> response = majorService.getAll();
        List<MajorResponse> majors = response.getData() == null ? List.of() : response.getData();

        return majors.stream()
                .map(ControllerSupport::toMap)
                .collect(Collectors.toMap(
                        item -> ControllerSupport.safeString(item.get("majorCode")),
                        item -> ControllerSupport.safeString(item.get("majorName")),
                        (left, right) -> left
                ));
    }

    private List<AdmissionPreferenceResponse> extractWishes(Map<String, Object> pageData) {
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
                new TypeReference<List<AdmissionPreferenceResponse>>() {
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
            pageInfoLabel.setText("Page " + (currentPage + 1) + "/" + Math.max(totalPages, 1) + " - " + totalElements + " wishes");
        }
        if (prevPageButton != null) {
            prevPageButton.setDisable(currentPage <= 0);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage + 1 >= totalPages);
        }
    }

    private WishRow toRow(AdmissionPreferenceResponse wish) {
        Map<String, Object> data = ControllerSupport.toMap(wish);
        String cccd = ControllerSupport.safeString(data.get("cccd"));
        String majorCode = ControllerSupport.safeString(data.get("majorCode"));
        int priorityOrder = parsePriorityOrder(data.get("priorityOrder"));

        return new WishRow(
                cccd,
                priorityOrder,
                majorNameByCode.getOrDefault(majorCode, majorCode),
                ControllerSupport.safeString(data.get("subjectGroup")),
                ControllerSupport.safeString(data.get("admissionScore")),
                ControllerSupport.safeString(data.get("result"))
        );
    }

    private int parsePriorityOrder(Object value) {
        if (value == null) {
            return 1;
        }
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ignored) {
            return 1;
        }
    }

    private String blankToNull(String value) {
        return ControllerSupport.trimToNull(value);
    }
}

