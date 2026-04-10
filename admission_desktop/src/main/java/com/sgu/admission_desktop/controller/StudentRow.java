package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.ReadOnlyStringProperty;

public class StudentRow {
    private final ReadOnlyStringWrapper maTs = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper hoTen = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper cccd = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper ngaySinh = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper email = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper sdt = new ReadOnlyStringWrapper();

    public StudentRow(String maTs, String hoTen, String cccd, String ngaySinh, String email, String sdt) {
        this.maTs.set(maTs);
        this.hoTen.set(hoTen);
        this.cccd.set(cccd);
        this.ngaySinh.set(ngaySinh);
        this.email.set(email);
        this.sdt.set(sdt);
    }

    public String maTs() {
        return maTs.get();
    }

    public String hoTen() {
        return hoTen.get();
    }

    public String cccd() {
        return cccd.get();
    }

    public ReadOnlyStringProperty maTsProperty() {
        return maTs.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty hoTenProperty() {
        return hoTen.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty cccdProperty() {
        return cccd.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty ngaySinhProperty() {
        return ngaySinh.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty emailProperty() {
        return email.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty sdtProperty() {
        return sdt.getReadOnlyProperty();
    }
}

