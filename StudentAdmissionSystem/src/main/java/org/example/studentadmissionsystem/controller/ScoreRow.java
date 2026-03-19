package org.example.studentadmissionsystem.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class ScoreRow {
    private final ReadOnlyStringWrapper maTs = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper hoTen = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper loaiDiem = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon1 = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon2 = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon3 = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tongDiem = new ReadOnlyStringWrapper();

    public ScoreRow(String maTs, String hoTen, String loaiDiem, String mon1, String mon2, String mon3, String tongDiem) {
        this.maTs.set(maTs);
        this.hoTen.set(hoTen);
        this.loaiDiem.set(loaiDiem);
        this.mon1.set(mon1);
        this.mon2.set(mon2);
        this.mon3.set(mon3);
        this.tongDiem.set(tongDiem);
    }

    public ReadOnlyStringProperty maTsProperty() {
        return maTs.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty hoTenProperty() {
        return hoTen.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty loaiDiemProperty() {
        return loaiDiem.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty mon1Property() {
        return mon1.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty mon2Property() {
        return mon2.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty mon3Property() {
        return mon3.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tongDiemProperty() {
        return tongDiem.getReadOnlyProperty();
    }
}

