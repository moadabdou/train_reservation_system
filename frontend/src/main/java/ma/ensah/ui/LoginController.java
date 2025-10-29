package ma.ensah.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ma.ensah.config.Session;
import ma.ensah.services.AuthService;

import java.util.concurrent.CompletableFuture;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitBtn;
    @FXML private Hyperlink toRegisterLink;
    @FXML private Button headerLoginBtn;
    @FXML private Button headerRegisterBtn;
    @FXML private Button headerLogoutBtn;

    private final AuthService auth = AuthService.defaultInstance();

    @FXML
    public void initialize() {
        toRegisterLink.setOnAction(e -> Navigation.goTo(stage(), "/views/RegisterView.fxml"));
        updateHeaderButtons();
    }

    @FXML
    public void goRegister() {
        Navigation.goTo(stage(), "/views/RegisterView.fxml");
    }

    @FXML
    public void goHome() {
        Navigation.goTo(stage(), "/views/MainView.fxml");
    }

    @FXML
    public void logout() {
        Session.clear();
        updateHeaderButtons();
    }

    private void updateHeaderButtons() {
        boolean authed = Session.isAuthenticated();
        if (headerLoginBtn != null && headerRegisterBtn != null && headerLogoutBtn != null) {
            headerLoginBtn.setVisible(!authed);
            headerLoginBtn.setManaged(!authed);
            headerRegisterBtn.setVisible(!authed);
            headerRegisterBtn.setManaged(!authed);
            headerLogoutBtn.setVisible(authed);
            headerLogoutBtn.setManaged(authed);
        }
    }

    @FXML
    public void onSubmit() {
        String email = emailField.getText();
        String pass = passwordField.getText();
        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            showError("Please enter email and password.");
            return;
        }
        submitBtn.setDisable(true);
        submitBtn.setText("Signing in...");

        CompletableFuture.runAsync(() -> {
            try {
                auth.login(email, pass);
                Platform.runLater(() -> Navigation.goTo(stage(), "/views/MainView.fxml"));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Login failed: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> { submitBtn.setDisable(false); submitBtn.setText("Sign in"); });
            }
        });
    }

    private Stage stage() { return (Stage) submitBtn.getScene().getWindow(); }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.showAndWait();
    }
}
