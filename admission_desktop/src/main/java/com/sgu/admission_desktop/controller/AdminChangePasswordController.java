package com.sgu.admission_desktop.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

import java.util.Objects;

public class AdminChangePasswordController {

    @FXML
    private PasswordField currentPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Button updateButton;

    @FXML
    private Label statusLabel;

    @FXML
    private void onUpdate() {
        String current = safeTrim(currentPasswordField);
        String next = safeTrim(newPasswordField);
        String confirm = safeTrim(confirmPasswordField);

        if (current.isEmpty() || next.isEmpty() || confirm.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ mật khẩu.");
            return;
        }

        if (next.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Mật khẩu quá ngắn", "Mật khẩu mới cần ít nhất 6 ký tự.");
            return;
        }

        if (!Objects.equals(next, confirm)) {
            showAlert(Alert.AlertType.WARNING, "Không khớp", "Mật khẩu xác nhận không đúng.");
            return;
        }

        // Demo-only: dự án hiện chưa có entity người dùng/admin để cập nhật DB.
        showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã đổi mật khẩu admin (demo).");
        clearFields();
    }

    @FXML
    private void onCancel() {
        clearFields();
    }

    private String safeTrim(PasswordField field) {
        return field == null || field.getText() == null ? "" : field.getText().trim();
    }

    private void clearFields() {
        if (currentPasswordField != null) currentPasswordField.clear();
        if (newPasswordField != null) newPasswordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
        if (statusLabel != null) statusLabel.setText("");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

