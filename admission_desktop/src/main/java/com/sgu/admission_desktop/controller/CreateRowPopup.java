package com.sgu.admission_desktop.controller;

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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public final class CreateRowPopup {
    private CreateRowPopup() {
    }

    public static Optional<Map<String, String>> show(String title, List<String> fields) {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText("Enter new data");

        ButtonType createButtonType = new ButtonType("Add data", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setPrefWidth(460);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(16));

        Map<String, TextField> inputs = new LinkedHashMap<>();
        for (int i = 0; i < fields.size(); i++) {
            String field = fields.get(i);
            Label label = new Label(field + ":");
            TextField textField = new TextField();
            textField.setPromptText("Enter " + field.toLowerCase(Locale.ROOT));
            grid.add(label, 0, i);
            grid.add(textField, 1, i);
            inputs.put(field, textField);
        }
        dialog.getDialogPane().setContent(grid);

        Node createButton = dialog.getDialogPane().lookupButton(createButtonType);
        createButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            List<String> missingRequiredFields = inputs.entrySet().stream()
                    .filter(entry -> !isOptionalField(entry.getKey()))
                    .filter(entry -> entry.getValue().getText().trim().isEmpty())
                    .map(Map.Entry::getKey)
                    .toList();

            if (!missingRequiredFields.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Missing required fields");
                alert.setHeaderText(null);
                alert.setContentText(
                        "Please fill required fields before adding data:\n"
                                + missingRequiredFields.stream().collect(Collectors.joining(", "))
                );
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

    private static boolean isOptionalField(String fieldName) {
        String normalized = fieldName.toLowerCase(Locale.ROOT);
        return normalized.contains("optional") || normalized.contains("tuy chon");
    }
}
