package me.ensah.ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import me.ensah.config.Session;
import me.ensah.ui.Navigation;
import me.ensah.ui.components.Btn;
import me.ensah.ui.components.SidebarItem;

public class SidebarController {

    @FXML
    private VBox sidebarContainer, menuBox, headerTextBox;
    @FXML
    private Label trainIcon;
    // @FXML
    // private Label adminNameLabel, adminPanelLabel;
    @FXML
    private Button toggleBtn;
    @FXML
    private SidebarItem logoutSidebarItem;

    private SidebarItem menuDashboard, menuUsers, menuTrains, menuSchedules, menuBookings;

    private boolean isCollapsed = false;
    private static final double EXPANDED_WIDTH = 270;
    private static final double COLLAPSED_WIDTH = 80;

    @FXML
    public void initialize() {
        // Show current admin name
        // if (Session.getCurrentUser() != null) {
        // adminNameLabel.setText("Welcome, " + Session.getCurrentUser().getName());
        // }

        // Create menu items programmatically
        createMenuItems();

        // Setup logout button
        logoutSidebarItem.setOnMouseClicked(e -> {
            Session.clear();
            Stage s = (Stage) sidebarContainer.getScene().getWindow();
            Navigation.goTo(s, "/fxml/LoginView.fxml");
        });

        // Toggle button
        // toggleBtn.setOnAction(e -> toggleSidebar());
    }

    private void createMenuItems() {
        menuBox.getChildren().clear();

        if (Session.isAdmin()) {
            menuDashboard = new SidebarItem("/icons/dashboard.svg", "Dashboard");
            menuUsers = new SidebarItem("/icons/users.svg", "Users");
            menuTrains = new SidebarItem("/icons/trains.svg", "Trains");
            menuSchedules = new SidebarItem("/icons/schedules.svg", "Schedules");
            menuBookings = new SidebarItem("/icons/bookings.svg", "Bookings");
            menuDashboard.setActive(true);

            menuBox.getChildren().addAll(
                    menuDashboard, menuUsers, menuTrains,
                    menuSchedules, menuBookings);

            setupAdminNavigation();
        } else {
            // Client Menu
            SidebarItem menuHome = new SidebarItem("/icons/dashboard.svg", "Home");
            SidebarItem menuMyBookings = new SidebarItem("/icons/bookings.svg", "My Bookings");

            // Default active
            menuHome.setActive(true);

            menuBox.getChildren().addAll(menuHome, menuMyBookings);

            menuHome.setOnMouseClicked(e -> {
                System.out.println("Navigating to Home");
                navigate(menuHome, "/fxml/MainView.fxml");
            });
            menuMyBookings.setOnMouseClicked(e -> {
                System.out.println("Navigating to My Bookings");
                navigate(menuMyBookings, "/fxml/BookingsView.fxml");
            });
        }
    }

    private void setupAdminNavigation() {
        menuDashboard.setOnMouseClicked(e -> navigate(menuDashboard, "/fxml/AdminDashboard.fxml"));
        menuUsers.setOnMouseClicked(e -> navigate(menuUsers, "/fxml/UsersManagement.fxml"));
        menuTrains.setOnMouseClicked(e -> navigate(menuTrains, "/fxml/TrainsManagement.fxml"));
        menuSchedules.setOnMouseClicked(e -> navigate(menuSchedules, "/fxml/SchedulesManagement.fxml"));
        menuBookings.setOnMouseClicked(e -> navigate(menuBookings, "/fxml/BookingsManagement.fxml"));
    }

    private void navigate(SidebarItem item, String fxmlPath) {
        System.out.println("Attempting navigation to: " + fxmlPath);
        setActiveItem(item);
        if (sidebarContainer.getScene() != null) {
            Stage s = (Stage) sidebarContainer.getScene().getWindow();
            System.out.println("Stage found: " + s);
            Navigation.goTo(s, fxmlPath);
        } else {
            System.err.println("SidebarContainer is not attached to a scene!");
        }
    }

    public void toggleSidebar() {
        isCollapsed = !isCollapsed;

        Timeline timeline = new Timeline();
        KeyValue kv = new KeyValue(sidebarContainer.prefWidthProperty(),
                isCollapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH);
        KeyFrame kf = new KeyFrame(Duration.millis(200), kv);
        timeline.getKeyFrames().add(kf);
        timeline.play();

        // Collapse header text
        headerTextBox.setVisible(!isCollapsed);
        headerTextBox.setManaged(!isCollapsed);

        // Show train icon when collapsed
        trainIcon.setVisible(isCollapsed);
        trainIcon.setManaged(isCollapsed);

        // Update toggle button position and style
        // updateToggleButton();

        // Collapse menu items
        menuBox.getChildren().forEach(node -> {
            if (node instanceof SidebarItem item) {
                item.setCollapsed(isCollapsed);
            }
        });

        // Update logout button for collapsed state
        // updateLogoutButton();
    }

    // private void updateToggleButton() {
    // toggleBtn.setText(isCollapsed ? "▶" : "◀");

    // // Adjust toggle button position when collapsed
    // if (isCollapsed) {
    // toggleBtn.setStyle(
    // "-fx-background-color: white; -fx-text-fill: #f97316; " +
    // "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; " +
    // "-fx-cursor: hand; -fx-min-width: 30; -fx-min-height: 30;");
    // } else {
    // toggleBtn.setStyle(
    // "-fx-background-color: white; -fx-text-fill: #f97316; " +
    // "-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8px; " +
    // "-fx-cursor: hand; -fx-min-width: 30; -fx-min-height: 30;");
    // }
    // }

    // private void updateLogoutButton() {
    // if (isCollapsed) {
    // // Make logout button icon-only when collapsed
    // logoutBtn.setText("🚪"); // Door icon instead of "Logout"
    // logoutBtn.setStyle(
    // "-fx-background-color: #ef4444; -fx-text-fill: white; " +
    // "-fx-font-weight: bold; -fx-background-radius: 8px; " +
    // "-fx-cursor: hand; -fx-padding: 12px; -fx-alignment: center;");
    // } else {
    // // Restore full logout button
    // logoutBtn.setText("Logout");
    // logoutBtn.setStyle(
    // "-fx-background-color: #ef4444; -fx-text-fill: white; " +
    // "-fx-font-weight: bold; -fx-background-radius: 8px; " +
    // "-fx-cursor: hand; -fx-padding: 12px;");
    // }
    // }

    private void setActiveItem(SidebarItem activeItem) {
        for (javafx.scene.Node node : menuBox.getChildren()) {
            if (node instanceof SidebarItem item) {
                item.setActive(false);
            }
        }
        activeItem.setActive(true);
    }
}