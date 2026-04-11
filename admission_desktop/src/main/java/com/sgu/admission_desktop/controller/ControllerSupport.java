package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.util.ObjectMapperUtil;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ControllerSupport {

    private ControllerSupport() {
    }

    public static void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static int parseInt(String value, String fieldName) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be an integer.");
        }
    }

    public static BigDecimal parseDecimal(String value, String fieldName) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    public static LocalDate parseDate(String value, String fieldName) {
        try {
            return LocalDate.parse(value.trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(fieldName + " must follow yyyy-MM-dd.");
        }
    }

    public static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static String formatDecimal(BigDecimal value) {
        if (value == null) {
            return "";
        }

        BigDecimal normalized = value.stripTrailingZeros();
        return normalized.scale() < 0 ? normalized.toPlainString() : normalized.toPlainString();
    }

    public static <T> T convert(Object source, Class<T> targetClass) {
        return ObjectMapperUtil.OBJECT_MAPPER.convertValue(source, targetClass);
    }

    public static <T> List<T> convertList(Object source, TypeReference<List<T>> typeReference) {
        if (source == null) {
            return Collections.emptyList();
        }
        return ObjectMapperUtil.OBJECT_MAPPER.convertValue(source, typeReference);
    }

    public static Map<String, Object> toMap(Object source) {
        if (source == null) {
            return Collections.emptyMap();
        }
        return ObjectMapperUtil.OBJECT_MAPPER.convertValue(source, new TypeReference<>() {
        });
    }

    public static String extractMessage(Throwable throwable) {
        Throwable cursor = throwable;
        while (cursor.getCause() != null) {
            cursor = cursor.getCause();
        }
        return cursor.getMessage() == null ? "Unknown error." : cursor.getMessage();
    }
}
