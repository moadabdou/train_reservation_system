package me.ensah.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import me.ensah.config.Session;
import me.ensah.services.AuthService;

import java.util.concurrent.CompletableFuture;

public class LoginController {
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button submitBtn;
    @FXML
    private Hyperlink toRegisterLink;
    @FXML
    private Button headerLoginBtn;
    @FXML
    private Button headerRegisterBtn;
    @FXML
    private Button headerLogoutBtn;

    private final AuthService auth = AuthService.defaultInstance();

    @FXML
    public void initialize() {
        toRegisterLink.setOnAction(e -> Navigation.goTo(stage(), "/fxml/RegisterView.fxml"));
        updateHeaderButtons();
    }

    @FXML
    public void goRegister() {
        Navigation.goTo(stage(), "/fxml/RegisterView.fxml");
    }

    @FXML
    public void goHome() {
        Navigation.goTo(stage(), "/fxml/MainView.fxml");
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
        // STEP 1: Get the email and password from the input fields
        String email = emailField.getText();
        String pass = passwordField.getText();

        // STEP 2: Validate that both fields are filled
        if (email == null || email.isBlank() || pass == null || pass.isBlank()) {
            showError("Please enter email and password.");
            return;
        }

        // STEP 3: Disable the button and show loading state
        submitBtn.setDisable(true);
        submitBtn.setText("Signing in...");

        // STEP 4: Run login in background thread (so UI doesn't freeze)
        CompletableFuture.runAsync(() -> {
            try {
                // STEP 5: Call AuthService to login (this sends HTTP request to backend)
                // This method will store the JWT token AND user info in Session
                auth.login(email, pass);

                // STEP 6: Check user role and redirect accordingly
                Platform.runLater(() -> {
                    if (Session.isAdmin()) {
                        // If user is admin, go to Admin Dashboard
                        System.out.println("Admin logged in: " + Session.getCurrentUser().getName());
                        Navigation.goTo(stage(), "/fxml/AdminDashboard.fxml");
                    } else {
                        // If user is client, go to Main View (booking page)
                        System.out.println("Client logged in: " + Session.getCurrentUser().getName());
                        Navigation.goTo(stage(), "/fxml/MainView.fxml");
                    }
                });

            } catch (Exception ex) {
                // STEP 7: If login fails, show error message
                Platform.runLater(() -> showError("Login failed: " + ex.getMessage()));
            } finally {
                // STEP 8: Re-enable the button and reset text (whether success or fail)
                Platform.runLater(() -> {
                    submitBtn.setDisable(false);
                    submitBtn.setText("Sign in");
                });
            }
        });
    }

    // Quick login methods for development
    @FXML
    public void quickAdminLogin() {
        emailField.setText("mohssine@gmail.com");
        passwordField.setText("mohssine");
        onSubmit();
    }

    @FXML
    public void quickClientLogin() {
        emailField.setText("a@a.com");
        passwordField.setText("a");
        onSubmit();
    }

    private Stage stage() {
        return (Stage) submitBtn.getScene().getWindow();
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.showAndWait();
    }
}
