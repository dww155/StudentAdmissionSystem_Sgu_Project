package com.sgu.data_quick_insert.model;

import java.util.List;

public record PreviewData(List<String> headers, List<List<String>> rows, long scannedRowCount) {

    public PreviewData {
        headers = List.copyOf(headers);
        rows = rows.stream().map(List::copyOf).toList();
    }
}
