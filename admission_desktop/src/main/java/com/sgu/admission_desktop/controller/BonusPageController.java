package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.ListAdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.service.AdmissionBonusScoreService;
import com.sgu.admission_desktop.service.ApplicantService;
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

public class BonusPageController implements Initializable {

    @FXML
    private TableView<BonusRow> table;

    @FXML
    private TableColumn<BonusRow, String> colMaTs;

    @FXML
    private TableColumn<BonusRow, String> colHoTen;

    @FXML
    private TableColumn<BonusRow, String> colDiemCong;

    @FXML
    private TableColumn<BonusRow, String> colLyDo;

    private final ObservableList<BonusRow> items = FXCollections.observableArrayList();
    private final AdmissionBonusScoreService admissionBonusScoreService = new AdmissionBonusScoreService();
    private final ApplicantService applicantService = new ApplicantService();
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

    private Map<String, String> applicantNameByCccd = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colDiemCong.setCellValueFactory(v -> v.getValue().diemCongProperty());
        colLyDo.setCellValueFactory(v -> v.getValue().lyDoProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadBonusScores();
    }

    private void loadBonusScores() {
        try {
            applicantNameByCccd = loadApplicantNames();

            ApiResponse<List<AdmissionBonusScoreResponse>> response = admissionBonusScoreService.getAll();
            List<AdmissionBonusScoreResponse> bonusScores = response.getData() == null ? List.of() : response.getData();

            items.setAll(bonusScores.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
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
            loadBonusScores();
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
            loadBonusScores();
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

    private BonusRow toRow(AdmissionBonusScoreResponse bonusScore) {
        Map<String, Object> data = ControllerSupport.toMap(bonusScore);
        String cccd = ControllerSupport.safeString(data.get("cccd"));
        return new BonusRow(
                cccd,
                applicantNameByCccd.getOrDefault(cccd, cccd),
                ControllerSupport.safeString(data.get("bonusScore")),
                ControllerSupport.safeString(data.get("note"))
        );
    }

    private String blankToNull(String value) {
        return ControllerSupport.trimToNull(value);
    }
}
