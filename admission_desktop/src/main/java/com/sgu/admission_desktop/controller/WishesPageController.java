package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionPreference.AdmissionPreferenceResponse;
import com.sgu.admission_desktop.dto.AdmissionPreference.ListAdmissionPreferenceCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.service.AdmissionPreferenceService;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.service.MajorService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

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
    private TableColumn<WishRow, String> colHoTen;

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

    private final ObservableList<WishRow> items = FXCollections.observableArrayList();
    private final AdmissionPreferenceService admissionPreferenceService = new AdmissionPreferenceService();
    private final ApplicantService applicantService = new ApplicantService();
    private final MajorService majorService = new MajorService();
    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.required("majorCode", "Major code", "ma nganh"),
            ExcelImportUtil.ColumnDefinition.required("priorityOrder", "Priority order", "nguyen vong", "thu tu nguyen vong"),
            ExcelImportUtil.ColumnDefinition.required("nvKeys", "NV keys", "nv key"),
            ExcelImportUtil.ColumnDefinition.optional("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.optional("subjectGroup", "Subject group", "to hop", "subject combination")
    );

    private Map<String, String> applicantNameByCccd = Map.of();
    private Map<String, String> majorNameByCode = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colNguyenVong.setCellValueFactory(v -> v.getValue().nguyenVongProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTongDiem.setCellValueFactory(v -> v.getValue().tongDiemProperty());
        colTrangThai.setCellValueFactory(v -> v.getValue().trangThaiProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadWishes();
    }

    private void loadWishes() {
        try {
            applicantNameByCccd = loadApplicantNames();
            majorNameByCode = loadMajorNames();

            ApiResponse<List<AdmissionPreferenceResponse>> response = admissionPreferenceService.getAll();
            List<AdmissionPreferenceResponse> wishes = response.getData() == null ? List.of() : response.getData();

            items.setAll(wishes.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
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
            loadWishes();
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
            loadWishes();
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

    private Map<String, String> loadApplicantNames() {
        ApiResponse<List<ApplicantResponse>> response = applicantService.getAll();
        List<ApplicantResponse> applicants = response.getData() == null ? List.of() : response.getData();

        return applicants.stream()
                .map(ControllerSupport::toMap)
                .collect(Collectors.toMap(
                        item -> ControllerSupport.safeString(item.get("cccd")),
                        item -> (ControllerSupport.safeString(item.get("lastName")) + " " + ControllerSupport.safeString(item.get("firstName"))).trim(),
                        (left, right) -> left
                ));
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

    private WishRow toRow(AdmissionPreferenceResponse wish) {
        Map<String, Object> data = ControllerSupport.toMap(wish);
        String cccd = ControllerSupport.safeString(data.get("cccd"));
        String majorCode = ControllerSupport.safeString(data.get("majorCode"));
        int priorityOrder = parsePriorityOrder(data.get("priorityOrder"));

        return new WishRow(
                cccd,
                applicantNameByCccd.getOrDefault(cccd, cccd),
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
