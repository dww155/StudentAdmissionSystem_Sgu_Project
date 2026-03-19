package org.example.studentadmissionsystem.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class BonusRow {
    private final ReadOnlyStringWrapper maTs = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper hoTen = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper diemCong = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper lyDo = new ReadOnlyStringWrapper();

    public BonusRow(String maTs, String hoTen, String diemCong, String lyDo) {
        this.maTs.set(maTs);
        this.hoTen.set(hoTen);
        this.diemCong.set(diemCong);
        this.lyDo.set(lyDo);
    }

    public ReadOnlyStringProperty maTsProperty() {
        return maTs.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty hoTenProperty() {
        return hoTen.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty diemCongProperty() {
        return diemCong.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty lyDoProperty() {
        return lyDo.getReadOnlyProperty();
    }
}

