package com.sgu.admission_desktop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class MajorSubjectsPageController implements Initializable {

    @FXML
    private TableView<MajorSubjectRow> table;

    @FXML
    private TableColumn<MajorSubjectRow, String> colMaNganh;

    @FXML
    private TableColumn<MajorSubjectRow, String> colTenNganh;

    @FXML
    private TableColumn<MajorSubjectRow, String> colMaToHop;

    @FXML
    private TableColumn<MajorSubjectRow, String> colTenToHop;

    private final ObservableList<MajorSubjectRow> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaNganh.setCellValueFactory(v -> v.getValue().maNganhProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTenToHop.setCellValueFactory(v -> v.getValue().tenToHopProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new MajorSubjectRow("N1", "Công nghệ thông tin", "T1", "Tổ hợp A00"),
                new MajorSubjectRow("N1", "Công nghệ thông tin", "T3", "Tổ hợp D01"),
                new MajorSubjectRow("N2", "Kinh tế", "T2", "Tổ hợp C00")
        );
    }
}

