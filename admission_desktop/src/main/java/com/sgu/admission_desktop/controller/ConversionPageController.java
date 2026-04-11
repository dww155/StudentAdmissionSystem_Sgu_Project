package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleCreationRequest;
import com.sgu.admission_desktop.dto.ConversionRule.ConversionRuleResponse;
import com.sgu.admission_desktop.service.ConversionRuleService;
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
