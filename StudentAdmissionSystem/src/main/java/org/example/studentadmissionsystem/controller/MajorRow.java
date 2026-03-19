package org.example.studentadmissionsystem.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class MajorRow {
    private final ReadOnlyStringWrapper maNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper chiTieu = new ReadOnlyStringWrapper();

    public MajorRow(String maNganh, String tenNganh, String chiTieu) {
        this.maNganh.set(maNganh);
        this.tenNganh.set(tenNganh);
        this.chiTieu.set(chiTieu);
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
}

