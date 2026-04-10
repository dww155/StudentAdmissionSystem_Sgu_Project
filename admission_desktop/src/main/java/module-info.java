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

    requires static lombok;
    requires java.net.http;

    // ✅ QUAN TRỌNG NHẤT
    opens com.sgu.admission_desktop.controller to javafx.fxml;

    // root package (an toàn)
    opens com.sgu.admission_desktop to javafx.fxml;

    exports com.sgu.admission_desktop;

    opens com.sgu.admission_desktop.dto.Authentication to com.fasterxml.jackson.databind;
    opens com.sgu.admission_desktop.dto to com.fasterxml.jackson.databind;
}