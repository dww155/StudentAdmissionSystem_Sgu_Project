package com.sgu.data_quick_insert.model;

public record InsertResult(
        long insertedRows,
        long skippedRows,
        long processedRows,
        int batchSize,
        String insertSql
) {
}
