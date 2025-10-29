package ma.ensah.ui;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class Navigation {
    private Navigation(){}

    public static void goTo(Stage stage, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(Navigation.class.getResource(fxmlPath));
            Scene scene = stage.getScene();
            if (scene == null) {
                scene = new Scene(root, 1100, 700);
                stage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
            // Ensure main stylesheet is applied
            if (scene.getStylesheets().isEmpty()) {
                scene.getStylesheets().add(Navigation.class.getResource("/styles/main.css").toExternalForm());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to navigate to " + fxmlPath, e);
        }
    }
}
