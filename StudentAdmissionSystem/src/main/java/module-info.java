module org.example.studentadmissionsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;

    opens org.example.studentadmissionsystem to javafx.fxml;
    exports org.example.studentadmissionsystem;
}