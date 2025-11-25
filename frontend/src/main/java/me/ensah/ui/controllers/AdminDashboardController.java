package me.ensah.ui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * AdminDashboardController
 * 
 * This controller manages the Admin Dashboard view.
 * It handles:
 * - Sidebar navigation between different admin sections
 * - Loading dashboard statistics
 * - Switching content based on selected menu item
 * - Logout functionality
 * 
 * LEARNING NOTES:
 * - @FXML annotation: Marks fields/methods that are linked to FXML elements
 * - fx:id in FXML: Links XML elements to Java fields
 * - Platform.runLater(): Updates UI from background threads (important for
 * JavaFX threading)
 */
public class AdminDashboardController {

    // ==================== FXML INJECTED FIELDS ====================
    // These fields are automatically filled by JavaFX when the FXML is loaded
    // The fx:id in FXML must match the field name here

    // NOTE: Sidebar menu items are now in SidebarController (separate component)
    // We only manage the center content area here

    // Content areas
    @FXML
    private VBox contentArea;
    @FXML
    private VBox dashboardContent;

    // Component controllers (injected via fx:id in FXML)
    @FXML
    private HeaderController headerComponentController;
    @FXML
    private SidebarController sidebarComponentController;

    // @FXML
    // private StackPane stickyHeaderPane;

    // @FXML
    // private ScrollPane scrollContent;

    // @FXML
    // private VBox sidebarContainer;

    // ==================== INITIALIZATION ====================

    /**
     * initialize() method is automatically called by JavaFX after FXML is loaded
     * This is where you set up initial state, load data, attach listeners, etc.
     * 
     * LIFECYCLE: Constructor -> FXML injection -> initialize() -> User interactions
     * 
     * NOTE: Sidebar navigation is now handled by SidebarController
     */
    @FXML
    public void initialize() {
        // Load dashboard statistics
        // Bind header width to ScrollPane width
        // stickyHeaderPane.prefWidthProperty().bind(scrollContent.widthProperty());
    }

    /**
     * Loads dashboard statistics (users count, trains count, bookings count)
     * 
     * LEARNING NOTE: In production, you would:
     * 1. Call backend API to get statistics
     * 2. Use CompletableFuture or Task to do it in background
     * 3. Update UI using Platform.runLater()
     * 
     * Example:
     * CompletableFuture.runAsync(() -> {
     * Stats stats = adminService.getStatistics();
     * Platform.runLater(() -> {
     * totalUsersLabel.setText(String.valueOf(stats.users));
     * totalTrainsLabel.setText(String.valueOf(stats.trains));
     * totalBookingsLabel.setText(String.valueOf(stats.bookings));
     * });
     * });
     */

}
