package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationCreationRequest;
import com.sgu.admission_desktop.dto.SubjectCombination.SubjectCombinationResponse;
import com.sgu.admission_desktop.service.SubjectCombinationService;
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

public class SubjectsPageController implements Initializable {

    @FXML
    private TableView<SubjectRow> table;

    @FXML
    private TableColumn<SubjectRow, String> colMaToHop;

    @FXML
    private TableColumn<SubjectRow, String> colTenToHop;

    @FXML
    private TableColumn<SubjectRow, String> colMon1;

    @FXML
    private TableColumn<SubjectRow, String> colMon2;

    @FXML
    private TableColumn<SubjectRow, String> colMon3;

    private final ObservableList<SubjectRow> items = FXCollections.observableArrayList();
    private final SubjectCombinationService subjectCombinationService = new SubjectCombinationService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTenToHop.setCellValueFactory(v -> v.getValue().tenToHopProperty());
        colMon1.setCellValueFactory(v -> v.getValue().mon1Property());
        colMon2.setCellValueFactory(v -> v.getValue().mon2Property());
        colMon3.setCellValueFactory(v -> v.getValue().mon3Property());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        loadSubjectCombinations();
    }

    private void loadSubjectCombinations() {
        try {
            ApiResponse<List<SubjectCombinationResponse>> response = subjectCombinationService.getAll();
            List<SubjectCombinationResponse> subjectCombinations = response.getData() == null ? List.of() : response.getData();

            items.setAll(subjectCombinations.stream()
                    .map(this::toRow)
                    .toList());
        } catch (Exception e) {
            items.clear();
            ControllerSupport.showError("Load subject combinations failed", ControllerSupport.extractMessage(e));
        }
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Add subject combination",
                        List.of("Code", "Name", "Subject 1", "Subject 2", "Subject 3")
                )
                .ifPresent(this::createSubjectCombination);
    }

    private void createSubjectCombination(Map<String, String> data) {
        try {
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("code", data.get("Code"));
            payload.put("name", data.get("Name"));
            payload.put("mon1", data.get("Subject 1"));
            payload.put("mon2", data.get("Subject 2"));
            payload.put("mon3", data.get("Subject 3"));

            SubjectCombinationCreationRequest request = ControllerSupport.convert(payload, SubjectCombinationCreationRequest.class);
            subjectCombinationService.create(request);
            loadSubjectCombinations();
        } catch (Exception e) {
            ControllerSupport.showError("Create subject combination failed", ControllerSupport.extractMessage(e));
        }
    }

    private SubjectRow toRow(SubjectCombinationResponse subjectCombination) {
        Map<String, Object> data = ControllerSupport.toMap(subjectCombination);
        return new SubjectRow(
                ControllerSupport.safeString(data.get("code")),
                ControllerSupport.safeString(data.get("name")),
                ControllerSupport.safeString(data.get("mon1")),
                ControllerSupport.safeString(data.get("mon2")),
                ControllerSupport.safeString(data.get("mon3"))
        );
    }
}
