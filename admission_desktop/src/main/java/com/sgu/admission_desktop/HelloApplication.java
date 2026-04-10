package com.sgu.admission_desktop;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("fxml/layouts/main-layout.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1600, 900);
        scene.getStylesheets().add(HelloApplication.class.getResource("styles/app.css").toExternalForm());
        stage.setTitle("Hệ thống tuyển sinh trường đại học Sài Gòn ^3^");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}