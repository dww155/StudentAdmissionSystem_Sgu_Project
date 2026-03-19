package org.example.studentadmissionsystem.controller;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class CreateRowPopup {
    private CreateRowPopup() {
    }

    public static Optional<Map<String, String>> show(String title, List<String> fields) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Nhập thông tin mới");

        ButtonType createButtonType = new ButtonType("Tạo mới", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        Map<String, TextField> inputs = new LinkedHashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Label label = new Label(field + ":");
            TextField textField = new TextField();
            textField.setPromptText("Nhập " + field.toLowerCase());
            grid.add(label, 0, i);
            grid.add(textField, 1, i);
            inputs.put(field, textField);
        }
        dialog.getDialogPane().setContent(grid);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            boolean hasEmpty = inputs.values().stream().anyMatch(tf -> tf.getText().trim().isEmpty());
            if (hasEmpty) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Thiếu thông tin");
                alert.setHeaderText(null);
                alert.setContentText("Vui lòng nhập đầy đủ thông tin trước khi tạo mới.");
                alert.showAndWait();
                event.consume();
            }
        });

        dialog.setResultConverter(buttonType -> {
            if (buttonType == createButtonType) {
                Map<String, String> result = new LinkedHashMap<>();
                for (Map.Entry<String, TextField> entry : inputs.entrySet()) {
                    result.put(entry.getKey(), entry.getValue().getText().trim());
                }
                return result;
            }
            return null;
        });

        return dialog.showAndWait();
    }
}
