package me.ensah.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import me.ensah.config.Session;
import me.ensah.ui.Navigation;
import me.ensah.ui.components.Btn;

public class HeaderController {

    @FXML
    private Btn loginBtn;

    @FXML
    private Label brandLabel;

    @FXML
    public Label sidebarToggle;//public so i can access it form main layout controller class

    // private SidebarController sidebarController;


    @FXML
    public void initialize() {
        updateLoginBtnState();

        // Setup click handlers
        setupClickHandlers();

        // Toggle button will be connected after sidebarController is set
        // sidebarToggle.setOnMouseClicked(e -> {
        //     if (sidebarController != null) {
        //         sidebarController.toggleSidebar();
        //     }
        // });
    }

    public void setToggleSidebarAction(Runnable action){/// Runnable is a functional interface means that 
        if (sidebarToggle != null) {
            sidebarToggle.setOnMouseClicked(e -> action.run());
        }
    }

    private void updateLoginBtnState() {
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
    }

    private void handleAuthAction() {
        if (Session.isLoggedIn()) {
            updateLoginBtnState();
             Session.clear();
            Stage s = (Stage) brandLabel.getScene().getWindow();
            Navigation.goTo(s, "/fxml/LoginView.fxml");
        } else {
            // Navigation to login page
            // Navi.showScene("LoginView.fxml");
        Navigation.goTo((javafx.stage.Stage) loginBtn.getScene().getWindow(), "/fxml/LoginView.fxml");

        }
    }

    // Public methods that can be called from parent controller
    public void refresh() {
        updateLoginBtnState();
    }

    // public void setSidebarController(SidebarController sidebarController) {
    //     this.sidebarController = sidebarController;
    //     // Connect toggle button after sidebar is available
    //     if (sidebarToggle != null && sidebarController != null) {
    //         sidebarToggle.setOnMouseClicked(e -> sidebarController.toggleSidebar());
    //     }
    // }

    public void setOnLoginAction(Runnable action) {
        if (loginBtn != null) {
            loginBtn.setOnAction(event -> action.run());
        }
    }
}
