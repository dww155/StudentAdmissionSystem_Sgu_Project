package com.sgu.data_quick_insert.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

public final class SqlIdentifierValidator {
    private static final Pattern SIMPLE_IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*");
    private static final Pattern TABLE_IDENTIFIER = Pattern.compile("[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*)?");

    private SqlIdentifierValidator() {
    }

    public static String requireValidTableName(String tableName) {
        String normalized = tableName == null ? "" : tableName.trim();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Table name is required.");
        }
        if (!TABLE_IDENTIFIER.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Invalid table name: '" + tableName + "'. Allowed pattern: schema.table or table."
            );
        }
        return normalized;
    }

    public static List<String> requireValidColumns(List<String> columns) {
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("Column list is required.");
        }

        List<String> normalized = new ArrayList<>(columns.size());
        Set<String> seen = new LinkedHashSet<>();

        for (String column : columns) {
            String name = column == null ? "" : column.trim();
            if (name.isEmpty()) {
                throw new IllegalArgumentException("Column names must not be empty.");
            }
            if (!SIMPLE_IDENTIFIER.matcher(name).matches()) {
                throw new IllegalArgumentException(
                        "Invalid column name: '" + name + "'. Use letters, numbers, underscore; start with a letter/underscore."
                );
            }
            String key = name.toLowerCase(Locale.ROOT);
            if (!seen.add(key)) {
                throw new IllegalArgumentException("Duplicate column name detected: '" + name + "'.");
            }
            normalized.add(name);
        }
        return List.copyOf(normalized);
    }

    public static boolean isBlankRow(List<String> row) {
        if (row == null || row.isEmpty()) {
            return true;
        }
        for (String cell : row) {
            if (cell != null && !cell.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
