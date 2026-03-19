package org.example.studentadmissionsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTenToHop.setCellValueFactory(v -> v.getValue().tenToHopProperty());
        colMon1.setCellValueFactory(v -> v.getValue().mon1Property());
        colMon2.setCellValueFactory(v -> v.getValue().mon2Property());
        colMon3.setCellValueFactory(v -> v.getValue().mon3Property());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new SubjectRow("T1", "Tổ hợp A00", "Toán", "Lý", "Hóa"),
                new SubjectRow("T2", "Tổ hợp C00", "Văn", "Sử", "Địa"),
                new SubjectRow("T3", "Tổ hợp D01", "Toán", "Văn", "Anh")
        );
    }
}

