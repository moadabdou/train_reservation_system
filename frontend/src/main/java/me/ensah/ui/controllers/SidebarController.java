package me.ensah.ui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import me.ensah.ui.components.SidebarItem;

public class SidebarController {

    private MainLayoutController mainLayoutController;

    @FXML
    private VBox sidebarContainer, menuBox, headerTextBox;
    @FXML
    private Label trainIcon;
    // @FXML
    // private SidebarItem logoutSidebarItem;

    private SidebarItem menuDashboard, menuUsers, menuTrains, menuSchedules, menuBookings;

    private boolean isCollapsed = false;
    private static final double EXPANDED_WIDTH = 270;
    private static final double COLLAPSED_WIDTH = 80;

    @FXML
    public void initialize() {
        createMenuItems();

        setupMenuNavigation();
    }

    private void createMenuItems() {
        menuDashboard = new SidebarItem("/icons/dashboard_transparent.png", "Dashboard");
        menuUsers = new SidebarItem("/icons/users_transparent.png", "Users");
        menuTrains = new SidebarItem("/icons/trains_transparent.png", "Trains");
        menuSchedules = new SidebarItem("/icons/schedules_transparent.png", "Schedules");
        menuBookings = new SidebarItem("/icons/bookings_transparent.png", "Bookings");
        menuDashboard.setActive(true);

        menuBox.getChildren().addAll(
                menuDashboard, menuUsers, menuTrains,
                menuSchedules, menuBookings);
    }

    private void setupMenuNavigation() {
        menuDashboard.setOnMouseClicked(e -> {
            // Deactivate all items
            deactivateAllItems();
            // Activate clicked item
            menuDashboard.setActive(true);
            // Load view
            mainLayoutController.loadHeader("Dashboard", "Welcome to your admin dashboard");
            mainLayoutController.loadView("/fxml/adminDashboard.fxml");
        });

        menuUsers.setOnMouseClicked(e -> {
            // Deactivate all items
            deactivateAllItems();
            // Activate clicked item
            menuUsers.setActive(true);
            // Load view
            mainLayoutController.loadHeader("Clients Management", "View and manage all registered clients");
            mainLayoutController.loadView("/fxml/usersManagement.fxml");
        });

        menuTrains.setOnMouseClicked(e -> {
            // Deactivate all items
            deactivateAllItems();
            // Activate clicked item
            menuTrains.setActive(true);
            // Load view - create dummy path or your actual trains view
            mainLayoutController.loadHeader("Trains Management", "Manage all trains in the system");
            // mainLayoutController.loadView("/fxml/TrainsManagement.fxml");
        });

        menuSchedules.setOnMouseClicked(e -> {
            // Deactivate all items
            deactivateAllItems();
            // Activate clicked item
            menuSchedules.setActive(true);
            // Load view - create dummy path or your actual schedules view
            mainLayoutController.loadHeader("Schedules Management", "Manage train schedules");
        });

        menuBookings.setOnMouseClicked(e -> {
            // Deactivate all items
            deactivateAllItems();
            // Activate clicked item
            menuBookings.setActive(true);
            // Load view - create dummy path or your actual bookings view
            mainLayoutController.loadHeader("Bookings Management", "View and manage all bookings");
            // mainLayoutController.loadView("/fxml/BookingsManagement.fxml");
        });
    }

    /**
     * Deactivate all menu items
     */
    private void deactivateAllItems() {
        menuDashboard.setActive(false);
        menuUsers.setActive(false);
        menuTrains.setActive(false);
        menuSchedules.setActive(false);
        menuBookings.setActive(false);
    }

    public void setMainLayoutController(MainLayoutController layoutController) {
        this.mainLayoutController = layoutController;
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

        // Collapse menu items
        menuBox.getChildren().forEach(node -> {
            if (node instanceof SidebarItem) {
                SidebarItem item = (SidebarItem) node;
                item.setCollapsed(isCollapsed);
            }
        });

    }
}