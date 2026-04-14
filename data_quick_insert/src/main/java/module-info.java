module com.sgu.data_quick_insert {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.xml;
    requires org.apache.poi.ooxml;

    opens com.sgu.data_quick_insert.controller to javafx.fxml;
    exports com.sgu.data_quick_insert;
}
