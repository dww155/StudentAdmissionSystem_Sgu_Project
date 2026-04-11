package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreCreationRequest;
import com.sgu.admission_desktop.dto.AdmissionBonusScore.AdmissionBonusScoreResponse;
import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.service.AdmissionBonusScoreService;
import com.sgu.admission_desktop.service.ApplicantService;
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
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }
}
