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

public class BonusPageController implements Initializable {

    @FXML
    private TableView<BonusRow> table;

    @FXML
    private TableColumn<BonusRow, String> colMaTs;

    @FXML
    private TableColumn<BonusRow, String> colHoTen;

    @FXML
    private TableColumn<BonusRow, String> colDiemCong;

    @FXML
    private TableColumn<BonusRow, String> colLyDo;

    private final ObservableList<BonusRow> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colDiemCong.setCellValueFactory(v -> v.getValue().diemCongProperty());
        colLyDo.setCellValueFactory(v -> v.getValue().lyDoProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new BonusRow("TS001", "Nguyễn Văn A", "1.5", "Giải khuyến khích"),
                new BonusRow("TS002", "Trần Thị B", "2.0", "Chứng chỉ ngoại ngữ"),
                new BonusRow("TS003", "Lê Văn C", "0.5", "Thành tích thể thao")
        );
    }

    @FXML
    private void onAddNew() {
        CreateRowPopup.show("Thêm điểm cộng", List.of("Mã TS", "Họ tên", "Điểm cộng", "Lý do"))
                .ifPresent(data -> items.add(mapToRow(data)));
    }

    private BonusRow mapToRow(Map<String, String> data) {
        return new BonusRow(
                data.get("Mã TS"),
                data.get("Họ tên"),
                data.get("Điểm cộng"),
                data.get("Lý do")
        );
    }
}

