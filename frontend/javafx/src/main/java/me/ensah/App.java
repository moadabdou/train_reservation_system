package me.ensah;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainView.fxml"));

        Scene scene = new Scene(root, 1100, 700);// Set initial size
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
        stage.setTitle("TrainLink");
        stage.setScene(scene);
        stage.setMaximized(true);// Maximize window while keeping controls visible
        stage.setMinWidth(800);// Set minimum width
        stage.setMinHeight(600);// Set minimum height
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}