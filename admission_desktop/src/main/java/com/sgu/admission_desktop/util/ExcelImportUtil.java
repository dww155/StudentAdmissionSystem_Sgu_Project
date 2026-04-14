package com.sgu.admission_desktop.util;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public final class ExcelImportUtil {

    private ExcelImportUtil() {
    }

    public static <T> Optional<List<T>> chooseAndRead(
            Window owner,
            String dialogTitle,
            List<ColumnDefinition> columns,
            RowMapper<T> rowMapper
    ) {
        Optional<Path> filePath = chooseExcelFile(owner, dialogTitle);
        if (filePath.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(readRows(filePath.get(), columns, rowMapper));
    }

    public static Optional<Path> chooseExcelFile(Window owner, String dialogTitle) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(dialogTitle);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx", "*.xls")
        );

        java.io.File file = fileChooser.showOpenDialog(owner);
        if (file == null) {
            return Optional.empty();
        }

        return Optional.of(file.toPath());
    }

    public static <T> List<T> readRows(
            Path filePath,
            List<ColumnDefinition> columns,
            RowMapper<T> rowMapper
    ) {
        try (InputStream inputStream = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            Sheet sheet = workbook.getNumberOfSheets() == 0 ? null : workbook.getSheetAt(0);
            if (sheet == null) {
                throw new IllegalArgumentException("The selected Excel file does not contain any sheets.");
            }

            DataFormatter formatter = new DataFormatter(Locale.ROOT);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            Row headerRow = findFirstNonEmptyRow(sheet, formatter, evaluator);

            if (headerRow == null) {
                throw new IllegalArgumentException("The selected Excel file is empty.");
            }

            Map<String, Integer> columnIndexes = resolveColumnIndexes(headerRow, columns, formatter, evaluator);
            List<T> rows = new ArrayList<>();

            for (int rowIndex = headerRow.getRowNum() + 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (isRowEmpty(row, formatter, evaluator)) {
                    continue;
                }

                Map<String, String> rowData = new LinkedHashMap<>();
                for (ColumnDefinition column : columns) {
                    rowData.put(column.key(), readCellValue(row, columnIndexes.get(column.key()), formatter, evaluator));
                }

                if (rowData.values().stream().allMatch(String::isBlank)) {
                    continue;
                }

                try {
                    rows.add(rowMapper.map(rowData));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Row " + (rowIndex + 1) + ": " + e.getMessage(), e);
                }
            }

            if (rows.isEmpty()) {
                throw new IllegalArgumentException("The selected Excel file does not contain any data rows.");
            }

            return rows;
        } catch (IOException e) {
            throw new RuntimeException("Cannot read the selected Excel file.", e);
        }
    }

    private static Row findFirstNonEmptyRow(Sheet sheet, DataFormatter formatter, FormulaEvaluator evaluator) {
        for (int rowIndex = sheet.getFirstRowNum(); rowIndex <= sheet.getLastRowNum(); rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (!isRowEmpty(row, formatter, evaluator)) {
                return row;
            }
        }
        return null;
    }

    private static Map<String, Integer> resolveColumnIndexes(
            Row headerRow,
            List<ColumnDefinition> columns,
            DataFormatter formatter,
            FormulaEvaluator evaluator
    ) {
        Map<String, Integer> availableHeaders = new LinkedHashMap<>();

        for (int cellIndex = 0; cellIndex < headerRow.getLastCellNum(); cellIndex++) {
            Cell cell = headerRow.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            String header = normalize(readCellValue(cell, formatter, evaluator));
            if (!header.isEmpty()) {
                availableHeaders.putIfAbsent(header, cellIndex);
            }
        }

        Map<String, Integer> columnIndexes = new LinkedHashMap<>();
        List<String> missingColumns = new ArrayList<>();

        for (ColumnDefinition column : columns) {
            Integer match = null;
            for (String alias : column.normalizedAliases()) {
                if (availableHeaders.containsKey(alias)) {
                    match = availableHeaders.get(alias);
                    break;
                }
            }

            if (match == null && column.required()) {
                missingColumns.add(column.label());
            }

            columnIndexes.put(column.key(), match);
        }

        if (!missingColumns.isEmpty()) {
            throw new IllegalArgumentException(
                    "Missing required columns in the first row: " + String.join(", ", missingColumns) + "."
            );
        }

        return columnIndexes;
    }

    private static boolean isRowEmpty(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null) {
            return true;
        }

        for (int cellIndex = row.getFirstCellNum(); cellIndex >= 0 && cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && !readCellValue(cell, formatter, evaluator).isBlank()) {
                return false;
            }
        }

        return true;
    }

    private static String readCellValue(Row row, Integer columnIndex, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (row == null || columnIndex == null) {
            return "";
        }

        Cell cell = row.getCell(columnIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        return readCellValue(cell, formatter, evaluator);
    }

    private static String readCellValue(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();
        if (cellType == CellType.FORMULA) {
            cellType = evaluator.evaluateFormulaCell(cell);
        }

        if (cellType == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getLocalDateTimeCellValue().toLocalDate().toString();
        }

        return formatter.formatCellValue(cell, evaluator).trim();
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }

        String normalized = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replace("\u0111", "d")
                .replace("\u0110", "d")
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);

        return normalized.replaceAll("[^a-z0-9]", "");
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(Map<String, String> row);
    }

    public record ColumnDefinition(String key, String label, boolean required, List<String> aliases) {

        public ColumnDefinition {
            aliases = aliases == null ? List.of() : List.copyOf(aliases);
        }

        public static ColumnDefinition required(String key, String label, String... aliases) {
            return new ColumnDefinition(key, label, true, List.of(aliases));
        }

        public static ColumnDefinition optional(String key, String label, String... aliases) {
            return new ColumnDefinition(key, label, false, List.of(aliases));
        }

        private Set<String> normalizedAliases() {
            Set<String> values = new LinkedHashSet<>();
            values.add(normalize(key));
            values.add(normalize(label));
            for (String alias : aliases) {
                values.add(normalize(alias));
            }
            values.remove("");
            return values;
        }
    }
}
