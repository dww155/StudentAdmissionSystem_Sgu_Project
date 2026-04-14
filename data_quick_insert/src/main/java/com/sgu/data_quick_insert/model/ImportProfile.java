package com.sgu.data_quick_insert.model;

import com.sgu.data_quick_insert.util.ExcelImportUtil;

import java.util.List;

public record ImportProfile(
        String key,
        String displayName,
        String tableName,
        List<ExcelImportUtil.ColumnDefinition> columnDefinitions
) {

    public ImportProfile {
        key = key == null ? "" : key.trim();
        displayName = displayName == null ? "" : displayName.trim();
        tableName = tableName == null ? "" : tableName.trim();
        columnDefinitions = columnDefinitions == null ? List.of() : List.copyOf(columnDefinitions);
    }

    public String columnHint() {
        return columnDefinitions.stream()
                .map(ExcelImportUtil.ColumnDefinition::label)
                .reduce((left, right) -> left + ", " + right)
                .orElse("");
    }
}
