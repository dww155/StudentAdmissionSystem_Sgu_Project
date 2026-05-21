package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class MajorRow {
    private final ReadOnlyStringWrapper maNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper chiTieu = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper soNguyenVong = new ReadOnlyStringWrapper();

    public MajorRow(String maNganh, String tenNganh, String chiTieu, String soNguyenVong) {
        this.maNganh.set(maNganh);
        this.tenNganh.set(tenNganh);
        this.chiTieu.set(chiTieu);
        this.soNguyenVong.set(soNguyenVong);
    }

    public ReadOnlyStringProperty maNganhProperty() {
        return maNganh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tenNganhProperty() {
        return tenNganh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty chiTieuProperty() {
        return chiTieu.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty soNguyenVongProperty() {
        return soNguyenVong.getReadOnlyProperty();
    }
}
