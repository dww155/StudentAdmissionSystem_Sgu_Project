package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.ListMajorCreationRequest;
import com.sgu.admission_desktop.dto.Major.MajorCreationRequest;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.admission_desktop.service.MajorService;
import com.sgu.admission_desktop.service.SubjectCombinationService;
import com.sgu.admission_desktop.util.ExcelImportUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class MajorsPageController implements Initializable {

    @FXML
    private TableView<MajorRow> table;

    @FXML
    private TableColumn<MajorRow, String> colMaNganh;

    @FXML
    private TableColumn<MajorRow, String> colTenNganh;

    @FXML
    private TableColumn<MajorRow, String> colChiTieu;

    private final ObservableList<MajorRow> items = FXCollections.observableArrayList();
    private final MajorService majorService = new MajorService();
    private final SubjectCombinationService subjectCombinationService = new SubjectCombinationService();
    private String importedDefaultBaseCombination;
    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaNganh.setCellValueFactory(v -> v.getValue().maNganhProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colChiTieu.setCellValueFactory(v -> v.getValue().chiTieuProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadMajors();
    }

    private void loadMajors() {
        try {
            ApiResponse<List<MajorResponse>> response = majorService.getAll();
            List<MajorResponse> majors = response.getData() == null ? List.of() : response.getData();

            items.setAll(majors.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
            ControllerSupport.showError("Load majors failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add major",
                        List.of("Major code", "Major name", "Base combination", "Quota", "Floor score", "Admission score")
                )
                .ifPresent(this::createMajor);
    }

    @FXML
    private void onImport() {
        try {
            var importedMajors = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import majors",
                    IMPORT_COLUMNS,
                    this::toImportedMajorRequest
            );

            if (importedMajors.isEmpty()) {
                return;
            }

            List<MajorCreationRequest> requests = importedMajors.get();
            majorService.createBulk(
                    ListMajorCreationRequest.builder()
                            .majorCreationRequestList(requests)
                            .build()
            );
            loadMajors();
            ControllerSupport.showInfo("Import majors", "Imported " + requests.size() + " majors from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import majors failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import majors failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createMajor(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("majorCode", data.get("Major code"));
            payload.put("majorName", data.get("Major name"));
            payload.put("baseCombination", data.get("Base combination"));
            payload.put("quota", ControllerSupport.parseInt(data.get("Quota"), "Quota"));
            payload.put("floorScore", ControllerSupport.parseDecimal(data.get("Floor score"), "Floor score"));
            payload.put("admissionScore", ControllerSupport.parseDecimal(data.get("Admission score"), "Admission score"));

            // Required by backend but not shown in current table layout.
            payload.put("directAdmission", "0");
            payload.put("dgnl", "0");
            payload.put("thpt", "0");
            payload.put("vsat", "0");
            payload.put("directAdmissionCount", 0);
            payload.put("competencyExamCount", 0);
            payload.put("vsatCount", 0);
            payload.put("highSchoolExamCount", "0");

            MajorCreationRequest request = ControllerSupport.convert(payload, MajorCreationRequest.class);
            majorService.create(request);
            loadMajors();
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid major", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create major failed", ControllerSupport.extractMessage(e));
        }
    }

    private MajorCreationRequest toImportedMajorRequest(Map<String, String> data) {
        BigDecimal floorScore = ControllerSupport.parseDecimal(data.get("floorScore"), "Ngưỡng đầu vào");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("majorCode", requireText(data.get("majorCode"), "Mã xét tuyển"));
        payload.put("majorName", requireText(data.get("majorName"), "Tên ngành, chương trình đào tạo"));
        payload.put("baseCombination", resolveImportedBaseCombination(data.get("baseCombination")));
        payload.put("quota", defaultInt(data.get("quota"), 0, "Quota"));
        payload.put("floorScore", floorScore);
        payload.put("admissionScore", defaultDecimal(data.get("admissionScore"), floorScore, "Admission score"));
        payload.put("directAdmission", defaultString(data.get("directAdmission"), "0"));
        payload.put("dgnl", defaultString(data.get("dgnl"), "0"));
        payload.put("thpt", defaultString(data.get("thpt"), "0"));
        payload.put("vsat", defaultString(data.get("vsat"), "0"));
        payload.put("directAdmissionCount", defaultInt(data.get("directAdmissionCount"), 0, "Direct admission count"));
        payload.put("competencyExamCount", defaultInt(data.get("competencyExamCount"), 0, "Competency exam count"));
        payload.put("vsatCount", defaultInt(data.get("vsatCount"), 0, "VSAT count"));
        payload.put("highSchoolExamCount", defaultString(data.get("highSchoolExamCount"), "0"));
        return ControllerSupport.convert(payload, MajorCreationRequest.class);
    }

    private MajorRow toRow(MajorResponse major) {
        Map<String, Object> data = ControllerSupport.toMap(major);
        return new MajorRow(
                ControllerSupport.safeString(data.get("majorCode")),
                ControllerSupport.safeString(data.get("majorName")),
                ControllerSupport.safeString(data.get("quota"))
        );
    }

    private String defaultString(String value, String defaultValue) {
        String trimmed = ControllerSupport.trimToNull(value);
        return trimmed == null ? defaultValue : trimmed;
    }

    private BigDecimal defaultDecimal(String value, BigDecimal defaultValue, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        return trimmed == null ? defaultValue : ControllerSupport.parseDecimal(trimmed, fieldName);
    }

    private int defaultInt(String value, int defaultValue, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        return trimmed == null ? defaultValue : ControllerSupport.parseInt(trimmed, fieldName);
    }

    private String requireText(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String resolveImportedBaseCombination(String value) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed != null) {
            return trimmed;
        }

        if (importedDefaultBaseCombination != null) {
            return importedDefaultBaseCombination;
        }

        ApiResponse<List<SubjectCombinationResponse>> response = subjectCombinationService.getAll();
        List<SubjectCombinationResponse> subjectCombinations = response.getData() == null ? List.of() : response.getData();
        importedDefaultBaseCombination = subjectCombinations.stream()
                .map(SubjectCombinationResponse::getCode)
                .map(ControllerSupport::trimToNull)
                .filter(code -> code != null)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No subject combination is available. Import subject combinations first or add a Base combination column."
                ));
        return importedDefaultBaseCombination;
    }
}
