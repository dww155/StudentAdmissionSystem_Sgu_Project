package org.example.studentadmissionsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class WishesPageController implements Initializable {

    @FXML
    private TableView<WishRow> table;

    @FXML
    private TableColumn<WishRow, String> colMaTs;

    @FXML
    private TableColumn<WishRow, String> colHoTen;

    @FXML
    private TableColumn<WishRow, String> colNguyenVong;

    @FXML
    private TableColumn<WishRow, String> colTenNganh;

    @FXML
    private TableColumn<WishRow, String> colMaToHop;

    @FXML
    private TableColumn<WishRow, String> colTongDiem;

    @FXML
    private TableColumn<WishRow, String> colTrangThai;

    private final ObservableList<WishRow> items = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colMaTs.setCellValueFactory(v -> v.getValue().maTsProperty());
        colHoTen.setCellValueFactory(v -> v.getValue().hoTenProperty());
        colNguyenVong.setCellValueFactory(v -> v.getValue().nguyenVongProperty());
        colTenNganh.setCellValueFactory(v -> v.getValue().tenNganhProperty());
        colMaToHop.setCellValueFactory(v -> v.getValue().maToHopProperty());
        colTongDiem.setCellValueFactory(v -> v.getValue().tongDiemProperty());
        colTrangThai.setCellValueFactory(v -> v.getValue().trangThaiProperty());

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setItems(items);

        seedDemoData();
    }

    private void seedDemoData() {
        items.setAll(
                new WishRow("TS001", "Nguyễn Văn A", 1, "Công nghệ thông tin", "T1", "23.0", "Chờ xét"),
                new WishRow("TS002", "Trần Thị B", 2, "Kinh tế", "T2", "27.0", "Trúng tuyển"),
                new WishRow("TS003", "Lê Văn C", 1, "Kế toán", "T3", "21.5", "Không đạt")
        );
    }
}

