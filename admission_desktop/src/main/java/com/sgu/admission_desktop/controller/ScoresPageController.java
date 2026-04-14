package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Applicant.ApplicantResponse;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreCreationRequest;
import com.sgu.admission_desktop.dto.ExamScore.ExamScoreResponse;
import com.sgu.admission_desktop.dto.ExamScore.ListExamScoreCreationRequest;
import com.sgu.admission_desktop.service.ApplicantService;
import com.sgu.admission_desktop.service.ExamScoreService;
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

    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.optional("stt", "STT"),
            ExcelImportUtil.ColumnDefinition.optional("registrationNumber", "Registration number", "registration", "ma ts", "mats", "so bao danh", "sbd"),
            ExcelImportUtil.ColumnDefinition.required("cccd", "CCCD"),
            ExcelImportUtil.ColumnDefinition.optional("program", "Chương trình", "chuong trinh", "program"),
            ExcelImportUtil.ColumnDefinition.optional("conversionCode", "Conversion code", "ma quy doi"),
            ExcelImportUtil.ColumnDefinition.optional("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.required("to", "TO", "toan", "math"),
            ExcelImportUtil.ColumnDefinition.required("li", "LI", "ly", "physics"),
            ExcelImportUtil.ColumnDefinition.required("ho", "HO", "hoa", "chemistry"),
            ExcelImportUtil.ColumnDefinition.optional("si", "SI", "sinh", "biology"),
            ExcelImportUtil.ColumnDefinition.optional("su", "SU", "history"),
            ExcelImportUtil.ColumnDefinition.optional("di", "DI", "dia", "geography"),
            ExcelImportUtil.ColumnDefinition.optional("va", "VA", "van", "literature"),
            ExcelImportUtil.ColumnDefinition.optional("n1Thi", "NN", "ngoai ngu", "foreign language", "n1 thi"),
            ExcelImportUtil.ColumnDefinition.optional("ktpl", "KTPL", "gdcd", "giao duc cong dan"),
            ExcelImportUtil.ColumnDefinition.optional("ti", "TI", "tieng trung", "chinese"),
            ExcelImportUtil.ColumnDefinition.optional("cncn", "CNCN"),
            ExcelImportUtil.ColumnDefinition.optional("cnnn", "CNNN"),
            ExcelImportUtil.ColumnDefinition.optional("nk1", "NK1"),
            ExcelImportUtil.ColumnDefinition.optional("nk2", "NK2")
    );

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

    @FXML
    private void onImport() {
        try {
            var importedScores = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import exam scores",
                    IMPORT_COLUMNS,
                    this::toImportedExamScoreRequest
            );

            if (importedScores.isEmpty()) {
                return;
            }

            List<ExamScoreCreationRequest> requests = importedScores.get();
            examScoreService.createBulk(
                    ListExamScoreCreationRequest.builder()
                            .examScoreCreationRequestList(requests)
                            .build()
            );
            loadScores();
            ControllerSupport.showInfo("Import exam scores", "Imported " + requests.size() + " exam scores from Excel.");
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import exam scores failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import exam scores failed", ControllerSupport.extractMessage(e));
        }
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

    private ExamScoreCreationRequest toImportedExamScoreRequest(Map<String, String> data) {
        String cccd = requireText(data.get("cccd"), "CCCD");
        String method = firstNonBlank(data.get("method"), data.get("program"), "THPT");
        String conversionCode = firstNonBlank(data.get("conversionCode"), data.get("program"), method);

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("cccd", cccd);
        payload.put("registrationNumber", firstNonBlank(data.get("registrationNumber"), cccd, data.get("stt")));
        payload.put("conversionCode", conversionCode);
        payload.put("method", method);
        payload.put("to", ControllerSupport.parseDecimal(data.get("to"), "TO"));
        payload.put("li", ControllerSupport.parseDecimal(data.get("li"), "LI"));
        payload.put("ho", ControllerSupport.parseDecimal(data.get("ho"), "HO"));
        payload.put("si", decimalOrZero(data.get("si"), "SI"));
        payload.put("su", decimalOrZero(data.get("su"), "SU"));
        payload.put("di", decimalOrZero(data.get("di"), "DI"));
        payload.put("va", decimalOrZero(data.get("va"), "VA"));
        payload.put("n1Thi", decimalOrZero(data.get("n1Thi"), "NN"));
        payload.put("n1Cc", BigDecimal.ZERO);
        payload.put("cncn", decimalOrZero(data.get("cncn"), "CNCN"));
        payload.put("cnnn", decimalOrZero(data.get("cnnn"), "CNNN"));
        payload.put("ti", decimalOrZero(data.get("ti"), "TI"));
        payload.put("ktpl", decimalOrZero(data.get("ktpl"), "KTPL"));
        payload.put("nl1", BigDecimal.ZERO);
        payload.put("nk1", decimalOrZero(data.get("nk1"), "NK1"));
        payload.put("nk2", decimalOrZero(data.get("nk2"), "NK2"));
        return ControllerSupport.convert(payload, ExamScoreCreationRequest.class);
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

    private BigDecimal decimalOrZero(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        return trimmed == null ? BigDecimal.ZERO : ControllerSupport.parseDecimal(trimmed, fieldName);
    }

    private String requireText(String value, String fieldName) {
        String trimmed = ControllerSupport.trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        return trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            String trimmed = ControllerSupport.trimToNull(value);
            if (trimmed != null) {
                return trimmed;
            }
        }
        return null;
    }
}
