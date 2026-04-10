package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class MajorSubjectRow {
    private final ReadOnlyStringWrapper maNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenNganh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper maToHop = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenToHop = new ReadOnlyStringWrapper();

    public MajorSubjectRow(String maNganh, String tenNganh, String maToHop, String tenToHop) {
        this.maNganh.set(maNganh);
        this.tenNganh.set(tenNganh);
        this.maToHop.set(maToHop);
        this.tenToHop.set(tenToHop);
    }

    public ReadOnlyStringProperty maNganhProperty() {
        return maNganh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tenNganhProperty() {
        return tenNganh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty maToHopProperty() {
        return maToHop.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tenToHopProperty() {
        return tenToHop.getReadOnlyProperty();
    }
}

