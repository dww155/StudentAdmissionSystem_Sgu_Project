package com.sgu.admission_desktop.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colLoaiDiem.setCellValueFactory(v -> v.getValue().loaiDiemProperty());
        colDiemGoc.setCellValueFactory(v -> v.getValue().diemGocProperty());
        colDiemQuyDoi.setCellValueFactory(v -> v.getValue().diemQuyDoiProperty());
        colHeSo.setCellValueFactory(v -> v.getValue().heSoProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new ConversionRow("THPT", "7.0", "8.4", "1.2"),
                new ConversionRow("VSAT", "8.0", "9.6", "1.2"),
                new ConversionRow("DGNL", "7.5", "8.5", "1.1")
        );
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show("Thêm quy đổi điểm", List.of("Loại điểm", "Điểm gốc", "Điểm quy đổi", "Hệ số"))
                .ifPresent(data -> items.add(mapToRow(data)));
    }

    private ConversionRow mapToRow(Map<String, String> data) {
        return new ConversionRow(
                data.get("Loại điểm"),
                data.get("Điểm gốc"),
                data.get("Điểm quy đổi"),
                data.get("Hệ số")
        );
    }
}

