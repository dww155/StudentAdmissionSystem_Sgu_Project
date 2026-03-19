package org.example.studentadmissionsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaNganh.setCellValueFactory(v -> v.getValue().maNganhProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colChiTieu.setCellValueFactory(v -> v.getValue().chiTieuProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new MajorRow("N1", "Công nghệ thông tin", "100"),
                new MajorRow("N2", "Kinh tế", "80"),
                new MajorRow("N3", "Kế toán", "60")
        );
    }
}

