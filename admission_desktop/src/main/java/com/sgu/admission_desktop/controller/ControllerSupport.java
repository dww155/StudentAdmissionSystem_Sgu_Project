package com.sgu.admission_desktop.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sgu.admission_desktop.util.ObjectMapperUtil;
import javafx.scene.control.Alert;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public final class ControllerSupport {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
            DateTimeFormatter.ISO_LOCAL_DATE,
            DateTimeFormatter.ofPattern("d/M/uuuu"),
            DateTimeFormatter.ofPattern("dd/MM/uuuu"),
            DateTimeFormatter.ofPattern("d-M-uuuu"),
            DateTimeFormatter.ofPattern("dd-MM-uuuu")
    );

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
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be an integer.");
        }
    }

    public static BigDecimal parseDecimal(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }
        try {
            String normalized = normalizeDecimal(value.trim());
            return new BigDecimal(normalized);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " must be a valid number.");
        }
    }

    public static LocalDate parseDate(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        String normalized = value.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(normalized, formatter);
            } catch (DateTimeParseException ignored) {
                // Try the next supported date format.
            }
        }

        throw new IllegalArgumentException(fieldName + " must follow yyyy-MM-dd or dd/MM/yyyy.");
    }

    public static String safeString(Object value) {
        return value == null ? "" : String.valueOf(value);
    }

    public static String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public static String formatDateValue(Object value) {
        if (value == null) {
            return "";
        }

        if (value instanceof LocalDate localDate) {
            return localDate.toString();
        }

        if (value instanceof List<?> parts && parts.size() >= 3) {
            try {
                int year = Integer.parseInt(String.valueOf(parts.get(0)));
                int month = Integer.parseInt(String.valueOf(parts.get(1)));
                int day = Integer.parseInt(String.valueOf(parts.get(2)));
                return LocalDate.of(year, month, day).toString();
            } catch (RuntimeException ignored) {
                // Fall through to default string conversion.
            }
        }

        return safeString(value);
    }

    private static String normalizeDecimal(String value) {
        String normalized = value.replace(" ", "");
        int commaIndex = normalized.lastIndexOf(',');
        int dotIndex = normalized.lastIndexOf('.');

        if (commaIndex >= 0 && dotIndex >= 0) {
            if (commaIndex > dotIndex) {
                return normalized.replace(".", "").replace(',', '.');
            }
            return normalized.replace(",", "");
        }

        if (commaIndex >= 0) {
            return normalized.replace(',', '.');
        }

        return normalized;
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
