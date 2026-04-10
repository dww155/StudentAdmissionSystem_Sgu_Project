package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class ConversionRow {
    private final ReadOnlyStringWrapper loaiDiem = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper diemGoc = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper diemQuyDoi = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper heSo = new ReadOnlyStringWrapper();

    public ConversionRow(String loaiDiem, String diemGoc, String diemQuyDoi, String heSo) {
        this.loaiDiem.set(loaiDiem);
        this.diemGoc.set(diemGoc);
        this.diemQuyDoi.set(diemQuyDoi);
        this.heSo.set(heSo);
    }

    public ReadOnlyStringProperty loaiDiemProperty() {
        return loaiDiem.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty diemGocProperty() {
        return diemGoc.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty diemQuyDoiProperty() {
        return diemQuyDoi.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty heSoProperty() {
        return heSo.getReadOnlyProperty();
    }
}

