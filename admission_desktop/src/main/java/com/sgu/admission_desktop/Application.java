package com.sgu.admission_desktop;

import com.sgu.admission_desktop.util.ApiClient;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("SGU Admission Desktop");
        showLoginLayout();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void showLoginLayout() {
        ApiClient.clearToken();
        setScene("fxml/layouts/login-layout.fxml", 980, 620);
    }

    public static void showMainLayout() {
        setScene("fxml/layouts/main-layout.fxml", 1600, 900);
    }

    private static void setScene(String resourcePath, int width, int height) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(resourcePath));
            Scene scene = new Scene(fxmlLoader.load(), width, height);
            scene.getStylesheets().add(Application.class.getResource("styles/app.css").toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load scene: " + resourcePath, e);
        }
    }
}
