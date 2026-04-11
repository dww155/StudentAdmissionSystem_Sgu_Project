package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.MajorCreationRequest;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.service.MajorService;
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

    private MajorRow toRow(MajorResponse major) {
        Map<String, Object> data = ControllerSupport.toMap(major);
        return new MajorRow(
                ControllerSupport.safeString(data.get("majorCode")),
                ControllerSupport.safeString(data.get("majorName")),
                ControllerSupport.safeString(data.get("quota"))
        );
    }
}
