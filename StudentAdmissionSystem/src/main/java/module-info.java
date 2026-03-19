module org.example.studentadmissionsystem {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;

    requires org.kordamp.ikonli.javafx;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires static lombok;

    opens org.example.studentadmissionsystem to javafx.fxml;
    exports org.example.studentadmissionsystem;
    exports org.example.studentadmissionsystem.controller;
    opens org.example.studentadmissionsystem.controller to javafx.fxml;
    opens org.example.studentadmissionsystem.model to org.hibernate.orm.core;
}