package com.sgu.admission_desktop.controller;

import com.sgu.admission_desktop.dto.ApiResponse;
import com.sgu.admission_desktop.dto.Major.MajorResponse;
import com.sgu.admission_desktop.service.MajorService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MajorSubjectsPageController implements Initializable {

    @FXML
    private TableView<MajorResponse> majorTable;

    @FXML
    private TableColumn<MajorResponse, String> colMaNganh;

    @FXML
    private TableColumn<MajorResponse, String> colTenNganh;

    @FXML
    private ListView<String> subjectGroupList;

    @FXML
    private Label rightPanelTitle;

    private final ObservableList<MajorResponse> majorItems = FXCollections.observableArrayList();
    private final ObservableList<String> subjectGroupItems = FXCollections.observableArrayList();

    private final MajorService majorService = new MajorService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaNganh.setCellValueFactory(v -> new ReadOnlyStringWrapper(safe(v.getValue().getMajorCode())));
        colTenNganh.setCellValueFactory(v -> new ReadOnlyStringWrapper(safe(v.getValue().getMajorName())));

        majorTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        majorTable.setItems(majorItems);

        subjectGroupList.setItems(subjectGroupItems);

        majorTable.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, selectedMajor) -> {
            renderSubjectGroups(selectedMajor);
        });

        loadMajors();
    }

    private void loadMajors() {
        try {
            ApiResponse<List<MajorResponse>> response = majorService.getAll();
            List<MajorResponse> majors = response.getData() == null ? List.of() : response.getData();
            majorItems.setAll(majors);

            if (!majors.isEmpty()) {
                majorTable.getSelectionModel().selectFirst();
                renderSubjectGroups(majors.get(0));
            } else {
                renderSubjectGroups(null);
            }
        } catch (Exception e) {
            majorItems.clear();
            renderSubjectGroups(null);
            ControllerSupport.showError("Load majors failed", ControllerSupport.extractMessage(e));
        }
    }

    private void renderSubjectGroups(MajorResponse selectedMajor) {
        subjectGroupItems.clear();

        if (selectedMajor == null) {
            rightPanelTitle.setText("To hop mon cua nganh da chon");
            return;
        }

        rightPanelTitle.setText("To hop mon - " + safe(selectedMajor.getMajorCode()) + " - " + safe(selectedMajor.getMajorName()));

        List<String> majorSubjectGroups = selectedMajor.getMajorSubjectGroups();
        if (majorSubjectGroups == null || majorSubjectGroups.isEmpty()) {
            subjectGroupItems.add("Khong co to hop mon");
            return;
        }

        subjectGroupItems.setAll(majorSubjectGroups);
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
