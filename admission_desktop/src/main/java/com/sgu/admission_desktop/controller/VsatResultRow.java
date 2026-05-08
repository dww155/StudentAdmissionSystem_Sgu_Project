package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class VsatResultRow {
    private final ReadOnlyStringWrapper cccd = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper dotThi = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper maDotThi = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper ngayThi = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper monThi = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper diem = new ReadOnlyStringWrapper();

    public VsatResultRow(String cccd, String dotThi, String maDotThi, String ngayThi, String monThi, String diem) {
        this.cccd.set(cccd);
        this.dotThi.set(dotThi);
        this.maDotThi.set(maDotThi);
        this.ngayThi.set(ngayThi);
        this.monThi.set(monThi);
        this.diem.set(diem);
    }

    public ReadOnlyStringProperty cccdProperty() {
        return cccd.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty dotThiProperty() {
        return dotThi.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty maDotThiProperty() {
        return maDotThi.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty ngayThiProperty() {
        return ngayThi.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty monThiProperty() {
        return monThi.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty diemProperty() {
        return diem.getReadOnlyProperty();
    }
}
