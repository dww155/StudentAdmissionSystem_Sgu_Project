package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreResponse;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.service.ExamScoreService;
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
import java.util.stream.Collectors;

public class ScoresPageController implements Initializable {

    @FXML
    private TableView<ScoreRow> table;

    @FXML
    private TableColumn<ScoreRow, String> colMaTs;

    @FXML
    private TableColumn<ScoreRow, String> colHoTen;

    @FXML
    private TableColumn<ScoreRow, String> colLoaiDiem;

    @FXML
    private TableColumn<ScoreRow, String> colMon1;

    @FXML
    private TableColumn<ScoreRow, String> colMon2;

    @FXML
    private TableColumn<ScoreRow, String> colMon3;

    @FXML
    private TableColumn<ScoreRow, String> colTongDiem;

    private final ObservableList<ScoreRow> items = FXCollections.observableArrayList();
    private final ExamScoreService examScoreService = new ExamScoreService();
    private final ApplicantService applicantService = new ApplicantService();

    private Map<String, String> applicantNameByCccd = Map.of();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colLoaiDiem.setCellValueFactory(v -> v.getValue().loaiDiemProperty());
        colMon1.setCellValueFactory(v -> v.getValue().mon1Property());
        colMon2.setCellValueFactory(v -> v.getValue().mon2Property());
        colMon3.setCellValueFactory(v -> v.getValue().mon3Property());
        colTongDiem.setCellValueFactory(v -> v.getValue().tongDiemProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadScores();
    }

    private void loadScores() {
        try {
            applicantNameByCccd = loadApplicantNames();

            ApiResponse<List<ExamScoreResponse>> response = examScoreService.getAll();
            List<ExamScoreResponse> scores = response.getData() == null ? List.of() : response.getData();

            items.setAll(scores.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
            ControllerSupport.showError("Load exam scores failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add exam score",
                        List.of("CCCD", "Registration number", "Conversion code", "Method", "Toan", "Ly", "Hoa")
                )
                .ifPresent(this::createExamScore);
    }

    private void createExamScore(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("cccd", data.get("CCCD"));
            payload.put("registrationNumber", data.get("Registration number"));
            payload.put("conversionCode", data.get("Conversion code"));
            payload.put("method", data.get("Method"));
            payload.put("to", ControllerSupport.parseDecimal(data.get("Toan"), "Toan"));
            payload.put("li", ControllerSupport.parseDecimal(data.get("Ly"), "Ly"));
            payload.put("ho", ControllerSupport.parseDecimal(data.get("Hoa"), "Hoa"));

            // Fill remaining required score slots with 0.
            payload.put("si", BigDecimal.ZERO);
            payload.put("su", BigDecimal.ZERO);
            payload.put("di", BigDecimal.ZERO);
            payload.put("va", BigDecimal.ZERO);
            payload.put("n1Thi", BigDecimal.ZERO);
            payload.put("n1Cc", BigDecimal.ZERO);
            payload.put("cncn", BigDecimal.ZERO);
            payload.put("cnnn", BigDecimal.ZERO);
            payload.put("ti", BigDecimal.ZERO);
            payload.put("ktpl", BigDecimal.ZERO);
            payload.put("nl1", BigDecimal.ZERO);
            payload.put("nk1", BigDecimal.ZERO);
            payload.put("nk2", BigDecimal.ZERO);

            ExamScoreCreationRequest request = ControllerSupport.convert(payload, ExamScoreCreationRequest.class);
            examScoreService.create(request);
            loadScores();
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid exam score", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create exam score failed", ControllerSupport.extractMessage(e));
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

    private ScoreRow toRow(ExamScoreResponse score) {
        Map<String, Object> data = ControllerSupport.toMap(score);
        String cccd = ControllerSupport.safeString(data.get("cccd"));

        return new ScoreRow(
                ControllerSupport.safeString(data.get("registrationNumber")),
                applicantNameByCccd.getOrDefault(cccd, cccd),
                ControllerSupport.safeString(data.get("method")),
                ControllerSupport.safeString(data.get("to")),
                ControllerSupport.safeString(data.get("li")),
                ControllerSupport.safeString(data.get("ho")),
                ControllerSupport.safeString(data.get("standardizedScore"))
        );
    }
}
