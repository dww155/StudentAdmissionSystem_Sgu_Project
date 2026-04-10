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

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new ScoreRow("TS001", "Nguyễn Văn A", "THPT", "8.0", "7.0", "7.5", "22.5"),
                new ScoreRow("TS002", "Trần Thị B", "VSAT", "9.0", "8.5", "8.0", "25.5"),
                new ScoreRow("TS003", "Lê Văn C", "DGNL", "8.5", "8.0", "7.0", "23.5")
        );
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show(
                        "Thêm điểm thí sinh",
                        List.of("Mã TS", "Họ tên", "Loại điểm", "Môn 1", "Môn 2", "Môn 3", "Tổng")
                )
                .ifPresent(data -> items.add(mapToRow(data)));
    }

    private ScoreRow mapToRow(Map<String, String> data) {
        return new ScoreRow(
                data.get("Mã TS"),
                data.get("Họ tên"),
                data.get("Loại điểm"),
                data.get("Môn 1"),
                data.get("Môn 2"),
                data.get("Môn 3"),
                data.get("Tổng")
        );
    }
}

