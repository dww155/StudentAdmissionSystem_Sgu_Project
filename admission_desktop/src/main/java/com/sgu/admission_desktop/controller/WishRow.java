package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class WishRow {
    private final ReadOnlyStringWrapper maTs = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper hoTen = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper nguyenVong = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper maToHop = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tongDiem = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper trangThai = new ReadOnlyStringWrapper();

    public WishRow(String maTs, String hoTen, int nguyenVong, String tenNganh, String maToHop, String tongDiem, String trangThai) {
        this.maTs.set(maTs);
        this.hoTen.set(hoTen);
        this.nguyenVong.set("NV" + nguyenVong);
        this.tenNganh.set(tenNganh);
        this.maToHop.set(maToHop);
        this.tongDiem.set(tongDiem);
        this.trangThai.set(trangThai);
    }

    public ReadOnlyStringProperty maTsProperty() {
        return maTs.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty hoTenProperty() {
        return hoTen.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty nguyenVongProperty() {
        return nguyenVong.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tenNganhProperty() {
        return tenNganh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty maToHopProperty() {
        return maToHop.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tongDiemProperty() {
        return tongDiem.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty trangThaiProperty() {
        return trangThai.getReadOnlyProperty();
    }
}

