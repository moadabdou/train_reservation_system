package ma.ensah.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ma.ensah.config.Session;
import ma.ensah.services.AuthService;

import java.util.concurrent.CompletableFuture;

public class RegisterController {
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button submitBtn;
    @FXML private Hyperlink toLoginLink;
    @FXML private Button headerLoginBtn;
    @FXML private Button headerRegisterBtn;
    @FXML private Button headerLogoutBtn;

    private final AuthService auth = AuthService.defaultInstance();

    @FXML
    public void initialize() {
        toLoginLink.setOnAction(e -> Navigation.goTo(stage(), "/views/LoginView.fxml"));
        updateHeaderButtons();
    }

    @FXML
    public void onSubmit() {
        String name = nameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();
        if (name == null || name.isBlank() || email == null || email.isBlank() || pass == null || pass.isBlank()) {
            showError("Please fill all fields.");
            return;
        }
        submitBtn.setDisable(true);
        submitBtn.setText("Creating account...");

        CompletableFuture.runAsync(() -> {
            try {
                auth.register(name, email, pass);
                Platform.runLater(() -> Navigation.goTo(stage(), "/views/MainView.fxml"));
            } catch (Exception ex) {
                Platform.runLater(() -> showError("Registration failed: " + ex.getMessage()));
            } finally {
                Platform.runLater(() -> { submitBtn.setDisable(false); submitBtn.setText("Create account"); });
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

    @FXML
    public void goLogin() { Navigation.goTo(stage(), "/views/LoginView.fxml"); }

    @FXML
    public void goHome() { Navigation.goTo(stage(), "/views/MainView.fxml"); }

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
}
