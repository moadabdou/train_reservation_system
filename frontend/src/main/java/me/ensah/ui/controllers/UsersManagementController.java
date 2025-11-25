package me.ensah.ui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import me.ensah.net.ApiClient;
import me.ensah.services.AdminClientService;
import me.ensah.model.User;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * UsersManagementController - Manages the admin clients/users page
 * 
 * Displays a table of all clients with their:
 * - ID
 * - Name
 * - Email
 * - Created Date
 * - Total Bookings
 * 
 * Features:
 * - Pagination (10 clients per page)
 * - Search by name or email
 * - Show statistics (total clients, total bookings)
 */
public class UsersManagementController {

    private AdminClientService adminClientService;
    @FXML
    private StackPane mainBorderPane;

    @FXML
    private TableView<User> clientsTable;

    @FXML
    private TextField searchField;

    @FXML
    private Label totalClientsLabel;

    @FXML
    private Label totalBookingsLabel;

    @FXML
    private Label pageInfoLabel;

    @FXML
    private Button prevBtn, nextBtn;

    // Pagination
    private int currentPage = 0;
    private static final int PAGE_SIZE = 10;
    private int totalPages = 0;
    private boolean isSearchMode = false;
    private String lastSearchTerm = "";

    // ==================== INITIALIZATION ====================

    @FXML
    public void initialize() {
        // Initialize API client and service
        ApiClient apiClient = new ApiClient("http://localhost:8080/api");
        adminClientService = new AdminClientService(apiClient);

        // Load initial data
        loadClients();
    }

    // ==================== DATA LOADING ====================

    /**
     * Load clients from API
     */
    private void loadClients() {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> response;
                if (isSearchMode && !lastSearchTerm.isEmpty()) {
                    response = adminClientService.searchClients(lastSearchTerm, currentPage, PAGE_SIZE);
                } else {
                    response = adminClientService.getAllClients(currentPage, PAGE_SIZE);
                }

                Platform.runLater(() -> {
                    displayClients(response);
                    updatePagination(response);
                    updateStatistics();
                });
            } catch (Exception e) {
                Platform.runLater(() -> showError("Failed to load clients: " + e.getMessage()));
            }
        });
    }

    /**
     * Display clients in the table
     */
    @SuppressWarnings("unchecked")
    private void displayClients(Map<String, Object> response) {
        ObservableList<User> clients = FXCollections.observableArrayList();

        if (response.containsKey("clients")) {
            List<Map<String, Object>> clientsList = (List<Map<String, Object>>) response.get("clients");

            System.out.println("Loading " + clientsList.size() + " clients into table...");

            for (Map<String, Object> clientMap : clientsList) {
                User user = new User();

                // Convert Map data to User object with null checking
                Object id = clientMap.get("id");
                if (id != null) {
                    user.setId(((Number) id).longValue());
                }

                user.setName((String) clientMap.get("name"));
                user.setEmail((String) clientMap.get("email"));
                user.setRole((String) clientMap.get("role"));

                // Handle createdAt - backend might send Date object or null
                Object createdAt = clientMap.get("createdAt");
                user.setCreatedAt(createdAt != null ? createdAt.toString() : "N/A");

                Object totalBookings = clientMap.get("totalBookings");
                if (totalBookings != null) {
                    user.setTotalBookings(((Number) totalBookings).intValue());
                } else {
                    user.setTotalBookings(0);
                }

                clients.add(user);
                System.out.println("Added user: " + user.getName() + " (" + user.getEmail() + ")");
            }
        }

        clientsTable.setItems(clients);
        System.out.println("Table updated with " + clients.size() + " User objects");
    }

    /**
     * Update pagination controls based on response
     */
    private void updatePagination(Map<String, Object> response) {
        double totalElements = ((Number) response.getOrDefault("totalElements", 0)).doubleValue();
        totalPages = (int) Math.ceil(totalElements / PAGE_SIZE);

        // Update page info label
        if (totalPages == 0) {
            pageInfoLabel.setText("No results");
            prevBtn.setDisable(true);
            nextBtn.setDisable(true);
        } else {
            pageInfoLabel.setText(String.format("Page %d of %d", currentPage + 1, totalPages));
            prevBtn.setDisable(currentPage == 0);
            nextBtn.setDisable(currentPage >= totalPages - 1);
        }
    }

    /**
     * Update statistics labels
     */
    private void updateStatistics() {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String, Object> stats = adminClientService.getDashboardStats();
                Platform.runLater(() -> {
                    double totalUsers = ((Number) stats.getOrDefault("totalUsers", 0)).doubleValue();
                    double totalBookings = ((Number) stats.getOrDefault("totalBookings", 0)).doubleValue();

                    totalClientsLabel.setText(String.valueOf((long) totalUsers));
                    totalBookingsLabel.setText(String.valueOf((long) totalBookings));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // ==================== EVENT HANDLERS ====================

    /**
     * Handle search button click
     */
    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showError("Please enter a search term");
            return;
        }

        isSearchMode = true;
        lastSearchTerm = searchTerm;
        currentPage = 0;
        loadClients();
    }

    /**
     * Handle reset button click
     */
    @FXML
    public void handleReset() {
        searchField.clear();
        isSearchMode = false;
        lastSearchTerm = "";
        currentPage = 0;
        loadClients();
    }

    /**
     * Handle previous page button
     */
    @FXML
    public void handlePrevious() {
        if (currentPage > 0) {
            currentPage--;
            loadClients();
        }
    }

    /**
     * Handle next page button
     */
    @FXML
    public void handleNext() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            loadClients();
        }
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Show error message to user
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success message to user
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
