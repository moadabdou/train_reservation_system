package me.ensah.ui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainLayoutController {

    @FXML
    private StackPane mainContent;

    @FXML
    private HeaderController headerComponentController;

    @FXML
    private SidebarController sidebarComponentController;

    @FXML
    private PageHeaderController pageHeaderComponentController;

    @FXML
    public void initialize() {
        if (sidebarComponentController != null) {
            sidebarComponentController.setMainLayoutController(this);
        }

        //
        BorderPane.setMargin(mainContent, new Insets(7, 0, 0, 0));
        
        loadHeader("Admin Dashboard", "Welcome to the admin dashboard");
        loadView("/fxml/adminDashboard.fxml");

        if (headerComponentController != null) {
            headerComponentController.setToggleSidebarAction(() -> {
                if (sidebarComponentController != null) {
                    sidebarComponentController.toggleSidebar();
                }
            });
        }

    }

    public void loadView(String fxmlPath) {
        try {
            Node node = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainContent.getChildren().setAll(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void loadHeader(String title, String subtitle) {
        if (pageHeaderComponentController != null) {
            pageHeaderComponentController.setTitle(title);
            pageHeaderComponentController.setSubtitle(subtitle);
        }
    }
}
