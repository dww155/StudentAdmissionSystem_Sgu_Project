package org.example.studentadmissionsystem.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class SubjectRow {
    private final ReadOnlyStringWrapper maToHop = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper tenToHop = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon1 = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon2 = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper mon3 = new ReadOnlyStringWrapper();

    public SubjectRow(String maToHop, String tenToHop, String mon1, String mon2, String mon3) {
        this.maToHop.set(maToHop);
        this.tenToHop.set(tenToHop);
        this.mon1.set(mon1);
        this.mon2.set(mon2);
        this.mon3.set(mon3);
    }

    public ReadOnlyStringProperty maToHopProperty() {
        return maToHop.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty tenToHopProperty() {
        return tenToHop.getReadOnlyProperty();
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
}

