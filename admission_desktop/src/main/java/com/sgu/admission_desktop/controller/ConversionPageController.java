package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.admission_desktop.dto.ConversionRule.ListConversionRuleCreationRequest;
import com.sgu.admission_desktop.service.ConversionRuleService;
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

public class ConversionPageController implements Initializable {

    @FXML
    private TableView<ConversionRow> table;

    @FXML
    private TableColumn<ConversionRow, String> colLoaiDiem;

    @FXML
    private TableColumn<ConversionRow, String> colDiemGoc;

    @FXML
    private TableColumn<ConversionRow, String> colDiemQuyDoi;

    @FXML
    private TableColumn<ConversionRow, String> colHeSo;

    private final ObservableList<ConversionRow> items = FXCollections.observableArrayList();
    private final ConversionRuleService conversionRuleService = new ConversionRuleService();
    private static final List<ExcelImportUtil.ColumnDefinition> IMPORT_COLUMNS = List.of(
            ExcelImportUtil.ColumnDefinition.required("method", "Method", "phuong thuc"),
            ExcelImportUtil.ColumnDefinition.required("subjectCombination", "Subject combination", "subject combination code", "to hop"),
            ExcelImportUtil.ColumnDefinition.required("subject", "Subject", "mon"),
            ExcelImportUtil.ColumnDefinition.required("diemA", "Diem A", "score a"),
            ExcelImportUtil.ColumnDefinition.required("diemB", "Diem B", "score b"),
            ExcelImportUtil.ColumnDefinition.required("diemC", "Diem C", "score c"),
            ExcelImportUtil.ColumnDefinition.required("diemD", "Diem D", "score d"),
            ExcelImportUtil.ColumnDefinition.required("conversionCode", "Conversion code", "ma quy doi"),
            ExcelImportUtil.ColumnDefinition.required("percentile", "Percentile", "bach phan vi")
    );

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colLoaiDiem.setCellValueFactory(v -> v.getValue().loaiDiemProperty());
        colDiemGoc.setCellValueFactory(v -> v.getValue().diemGocProperty());
        colDiemQuyDoi.setCellValueFactory(v -> v.getValue().diemQuyDoiProperty());
        colHeSo.setCellValueFactory(v -> v.getValue().heSoProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadConversionRules();
    }

    private void loadConversionRules() {
        try {
            ApiResponse<List<ConversionRuleResponse>> response = conversionRuleService.getAll();
            List<ConversionRuleResponse> rules = response.getData() == null ? List.of() : response.getData();

            items.setAll(rules.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
            ControllerSupport.showError("Load conversion rules failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add conversion rule",
                        List.of("Method", "Subject combination", "Subject", "Diem A", "Diem B", "Diem C", "Diem D", "Conversion code", "Percentile")
                )
                .ifPresent(this::createConversionRule);
    }

    @FXML
    private void onImport() {
        try {
            var importedConversionRules = ExcelImportUtil.chooseAndRead(
                    table.getScene() == null ? null : table.getScene().getWindow(),
                    "Import conversion rules",
                    IMPORT_COLUMNS,
                    this::toImportedConversionRuleRequest
            );

            if (importedConversionRules.isEmpty()) {
                return;
            }

            List<ConversionRuleCreationRequest> requests = importedConversionRules.get();
            conversionRuleService.createBulk(
                    ListConversionRuleCreationRequest.builder()
                            .conversionRuleCreationRequestList(requests)
                            .build()
            );
            loadConversionRules();
            ControllerSupport.showInfo(
                    "Import conversion rules",
                    "Imported " + requests.size() + " conversion rules from Excel."
            );
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Import conversion rules failed", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Import conversion rules failed", ControllerSupport.extractMessage(e));
        }
    }

    private void createConversionRule(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("method", data.get("Method"));
            payload.put("subjectCombination", data.get("Subject combination"));
            payload.put("subject", data.get("Subject"));
            payload.put("diemA", ControllerSupport.parseDecimal(data.get("Diem A"), "Diem A"));
            payload.put("diemB", ControllerSupport.parseDecimal(data.get("Diem B"), "Diem B"));
            payload.put("diemC", ControllerSupport.parseDecimal(data.get("Diem C"), "Diem C"));
            payload.put("diemD", ControllerSupport.parseDecimal(data.get("Diem D"), "Diem D"));
            payload.put("conversionCode", data.get("Conversion code"));
            payload.put("percentile", data.get("Percentile"));

            ConversionRuleCreationRequest request = ControllerSupport.convert(payload, ConversionRuleCreationRequest.class);
            conversionRuleService.create(request);
            loadConversionRules();
        } catch (IllegalArgumentException e) {
            ControllerSupport.showError("Invalid conversion rule", e.getMessage());
        } catch (Exception e) {
            ControllerSupport.showError("Create conversion rule failed", ControllerSupport.extractMessage(e));
        }
    }

    private ConversionRuleCreationRequest toImportedConversionRuleRequest(Map<String, String> data) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("method", data.get("method"));
        payload.put("subjectCombination", data.get("subjectCombination"));
        payload.put("subject", data.get("subject"));
        payload.put("diemA", ControllerSupport.parseDecimal(data.get("diemA"), "Diem A"));
        payload.put("diemB", ControllerSupport.parseDecimal(data.get("diemB"), "Diem B"));
        payload.put("diemC", ControllerSupport.parseDecimal(data.get("diemC"), "Diem C"));
        payload.put("diemD", ControllerSupport.parseDecimal(data.get("diemD"), "Diem D"));
        payload.put("conversionCode", data.get("conversionCode"));
        payload.put("percentile", data.get("percentile"));
        return ControllerSupport.convert(payload, ConversionRuleCreationRequest.class);
    }

    private ConversionRow toRow(ConversionRuleResponse rule) {
        Map<String, Object> data = ControllerSupport.toMap(rule);
        String loaiDiem = ControllerSupport.safeString(data.get("method")) + " - " + ControllerSupport.safeString(data.get("subject"));
        return new ConversionRow(
                loaiDiem,
                ControllerSupport.safeString(data.get("diemA")),
                ControllerSupport.safeString(data.get("diemB")),
                ControllerSupport.safeString(data.get("percentile"))
        );
    }
}
