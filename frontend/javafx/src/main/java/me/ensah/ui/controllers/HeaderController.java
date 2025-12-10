package me.ensah.ui.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Label;
import me.ensah.config.Session;
import me.ensah.ui.Navigation;
import me.ensah.ui.components.Btn;

public class HeaderController {

    @FXML
    private Btn loginBtn;

    @FXML
    private Label brandLabel, sidebarToggle;

    private SidebarController sidebarController;

    @FXML
    public void initialize() {
        // Initialize button state based on session
        updateButtonState();

        // Setup click handlers
        setupClickHandlers();

        // Toggle button will be connected after sidebarController is set
    }

    private void updateButtonState() {
        if (loginBtn != null) {
            if (Session.isLoggedIn()) {
                loginBtn.setText("Logout");
                loginBtn.setType("SECONDARY");
            } else {
                loginBtn.setText("Login");
                loginBtn.setType("PRIMARY");
            }
        }
    }

    private void setupClickHandlers() {
        // Login/Logout button handler
        if (loginBtn != null) {
            loginBtn.setOnAction(event -> handleAuthAction());
        }

        // if (brandLabel != null) {
        // brandLabel.setOnMouseClicked(event -> goHome());
        // }
    }

    private void handleAuthAction() {
        if (Session.isLoggedIn()) {
            // Logout
            Session.clear();
            updateButtonState();
            // Optionally navigate to home or login page
            // ViewManager.showScene("LoginView.fxml");
        } else {
            // Navigation to login page
            // Navi.showScene("LoginView.fxml");
            Navigation.goTo((javafx.stage.Stage) loginBtn.getScene().getWindow(), "/fxml/RegisterView.fxml");

        }
    }

    // Public methods that can be called from parent controller
    public void refresh() {
        updateButtonState();
    }

    public void setSidebarController(SidebarController sidebarController) {
        this.sidebarController = sidebarController;
        // Connect toggle button after sidebar is available
        if (sidebarToggle != null && sidebarController != null) {
            sidebarToggle.setOnMouseClicked(e -> sidebarController.toggleSidebar());
        }
    }

    public void setOnSidebarToggle(Runnable action) {
        if (sidebarToggle != null) {
            sidebarToggle.setOnMouseClicked(e -> action.run());
        }
    }

    public void setOnLoginAction(Runnable action) {
        if (loginBtn != null) {
            loginBtn.setOnAction(event -> action.run());
        }
    }
}
