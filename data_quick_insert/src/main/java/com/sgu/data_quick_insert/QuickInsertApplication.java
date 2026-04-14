package com.sgu.data_quick_insert;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuickInsertApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(
                QuickInsertApplication.class.getResource("/com/sgu/data_quick_insert/main-view.fxml")
        );
        Scene scene = new Scene(loader.load(), 1200, 760);
        stage.setTitle("Excel Quick Insert");
        stage.setScene(scene);
        stage.setMinWidth(980);
        stage.setMinHeight(640);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
