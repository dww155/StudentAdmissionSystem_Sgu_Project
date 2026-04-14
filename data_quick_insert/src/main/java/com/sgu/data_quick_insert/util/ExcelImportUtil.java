package com.sgu.data_quick_insert.util;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class ExcelImportUtil {

    private ExcelImportUtil() {
    }

    public static ReadStats forEachMappedRow(
            java.nio.file.Path filePath,
            String sheetName,
            List<ColumnDefinition> columns,
            MappedRowConsumer rowConsumer
    ) throws Exception {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Column definition list is required.");
        }

        final Holder holder = new Holder();
        holder.columns = List.copyOf(columns);

        ExcelStreamingReader.streamRows(filePath, sheetName, (rowNumber, rowValues) -> {
            if (SqlIdentifierValidator.isBlankRow(rowValues)) {
                holder.skippedRows++;
                return;
            }

            if (!holder.headerResolved) {
                holder.columnIndexes = resolveColumnIndexes(rowValues, holder.columns);
                holder.headerResolved = true;
                return;
            }

            Map<String, String> mappedData = new LinkedHashMap<>();
            for (ColumnDefinition column : holder.columns) {
                Integer index = holder.columnIndexes.get(column.key());
                String value = "";
                if (index != null && index >= 0 && index < rowValues.size()) {
                    value = rowValues.get(index) == null ? "" : rowValues.get(index).trim();
                }
                mappedData.put(column.key(), value);
            }

            if (mappedData.values().stream().allMatch(String::isBlank)) {
                holder.skippedRows++;
                return;
            }

            rowConsumer.accept(rowNumber, mappedData);
            holder.emittedRows++;
        });

        if (!holder.headerResolved) {
            throw new IllegalArgumentException("The selected Excel file is empty.");
        }
        if (holder.emittedRows == 0) {
            throw new IllegalArgumentException("The selected Excel file does not contain any data rows.");
        }

        return new ReadStats(holder.emittedRows, holder.skippedRows);
    }

    private static Map<String, Integer> resolveColumnIndexes(
            List<String> headerRow,
            List<ColumnDefinition> columns
    ) {
        Map<String, Integer> availableHeaders = new LinkedHashMap<>();
        for (int i = 0; i < headerRow.size(); i++) {
            String normalizedHeader = normalize(headerRow.get(i));
            if (!normalizedHeader.isEmpty()) {
                availableHeaders.putIfAbsent(normalizedHeader, i);
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
    public interface MappedRowConsumer {
        void accept(long rowNumber, Map<String, String> rowData) throws Exception;
    }

    public record ReadStats(long emittedRows, long skippedRows) {
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

    private static final class Holder {
        private List<ColumnDefinition> columns;
        private Map<String, Integer> columnIndexes;
        private boolean headerResolved;
        private long emittedRows;
        private long skippedRows;
    }
}
