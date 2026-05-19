package com.sgu.admission_desktop.controller;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PriorityBonusPointRow {
    private final SimpleIntegerProperty id;
    private final StringProperty cccd;
    private final StringProperty level;
    private final StringProperty team;
    private final StringProperty subjectCode;
    private final StringProperty prizeType;
    private final StringProperty bonusPointForSubject;
    private final StringProperty bonusPointForSubjectGroup;

    public PriorityBonusPointRow(
            Integer id,
            String cccd,
            String level,
            String team,
            String subjectCode,
            String prizeType,
            String bonusPointForSubject,
            String bonusPointForSubjectGroup
    ) {
        this.id = new SimpleIntegerProperty(id == null ? 0 : id);
        this.cccd = new SimpleStringProperty(cccd == null ? "" : cccd);
        this.level = new SimpleStringProperty(level == null ? "" : level);
        this.team = new SimpleStringProperty(team == null ? "" : team);
        this.subjectCode = new SimpleStringProperty(subjectCode == null ? "" : subjectCode);
        this.prizeType = new SimpleStringProperty(prizeType == null ? "" : prizeType);
        this.bonusPointForSubject = new SimpleStringProperty(bonusPointForSubject == null ? "" : bonusPointForSubject);
        this.bonusPointForSubjectGroup = new SimpleStringProperty(bonusPointForSubjectGroup == null ? "" : bonusPointForSubjectGroup);
    }

    public int id() {
        return id.get();
    }

    public StringProperty cccdProperty() {
        return cccd;
    }

    public StringProperty levelProperty() {
        return level;
    }

    public StringProperty teamProperty() {
        return team;
    }

    public StringProperty subjectCodeProperty() {
        return subjectCode;
    }

    public StringProperty prizeTypeProperty() {
        return prizeType;
    }

    public StringProperty bonusPointForSubjectProperty() {
        return bonusPointForSubject;
    }

    public StringProperty bonusPointForSubjectGroupProperty() {
        return bonusPointForSubjectGroup;
    }
}
