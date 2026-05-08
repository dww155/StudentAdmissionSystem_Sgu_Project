package com.sgu.admission_desktop.controller;

import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;

public class EnglishCertificationRow {
    private final ReadOnlyStringWrapper cccd = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper certificationName = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper certificationScore = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper conversionScore = new ReadOnlyStringWrapper();
    private final ReadOnlyStringWrapper bonusScore = new ReadOnlyStringWrapper();

    public EnglishCertificationRow(
            String cccd,
            String certificationName,
            String certificationScore,
            String conversionScore,
            String bonusScore
    ) {
        this.cccd.set(cccd);
        this.certificationName.set(certificationName);
        this.certificationScore.set(certificationScore);
        this.conversionScore.set(conversionScore);
        this.bonusScore.set(bonusScore);
    }

    public ReadOnlyStringProperty cccdProperty() {
        return cccd.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty certificationNameProperty() {
        return certificationName.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty certificationScoreProperty() {
        return certificationScore.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty conversionScoreProperty() {
        return conversionScore.getReadOnlyProperty();
    }

    public ReadOnlyStringProperty bonusScoreProperty() {
        return bonusScore.getReadOnlyProperty();
    }
}
