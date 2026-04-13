module com.sgu.admission_desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires poi;
    requires poi.ooxml;

    requires static lombok;
    requires java.net.http;
    requires jakarta.validation;

    opens com.sgu.admission_desktop.controller to javafx.fxml;
    opens com.sgu.admission_desktop to javafx.fxml;

    exports com.sgu.admission_desktop;

    opens com.sgu.admission_desktop.dto to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.Authentication to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.AdmissionBonusScore to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.AdmissionPreference to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.Applicant to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.ConversionRule to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.ExamScore to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.Major to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.MajorSubjectGroup to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.SubjectCombination to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto.user to com.fasterxml.jackson.databind;
}
