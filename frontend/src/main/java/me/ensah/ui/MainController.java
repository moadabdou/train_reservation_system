package me.ensah.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import me.ensah.model.Schedule;
import me.ensah.net.ApiClient;
import me.ensah.services.ScheduleService;
import me.ensah.services.StationService;
import me.ensah.ui.TrainCardCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import javafx.application.Platform;

/**
 * MainController - Handles the main search page for train schedules
 * 
 * Features:
 * - Search trains by departure/arrival stations and date
 * - Display available schedules
 * - Navigate to booking when train is selected
 */
public class MainController {

    @FXML
    private ComboBox<String> fromStation;

    @FXML
    private ComboBox<String> toStation;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button searchBtn;

    @FXML
    private ListView<Schedule> trainList;

    @FXML
    private Label fabMenu;

    @FXML
    private VBox sidePanel;

    private ApiClient apiClient;
    private ScheduleService scheduleService;
    private StationService stationService;

    @FXML
    public void initialize() {
        // Initialize API services
        apiClient = new ApiClient("http://localhost:8080/api");
        scheduleService = new ScheduleService(apiClient);
        stationService = new StationService(apiClient);

        // Load stations into combo boxes
        loadStations();

        // Set up list cell factory
        trainList.setCellFactory(param -> new TrainCardCell());

        // Set initial date to today
        datePicker.setValue(LocalDate.now());
    }

    /**
     * Load all stations and populate combo boxes
     */
    private void loadStations() {
        CompletableFuture.runAsync(() -> {
            try {
                List<me.ensah.model.Station> stations = stationService.fetchStations();
                Platform.runLater(() -> {
                    ObservableList<String> stationNames = FXCollections.observableArrayList();
                    stations.forEach(s -> stationNames.add(s.getName()));

                    fromStation.setItems(stationNames);
                    toStation.setItems(stationNames);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Handle search button click
     */
    @FXML
    public void onSearch() {
        String from = fromStation.getValue();
        String to = toStation.getValue();
        LocalDate date = datePicker.getValue();

        if (from == null || to == null || date == null) {
            showError("Please select departure station, arrival station, and date");
            return;
        }

        if (from.equals(to)) {
            showError("Departure and arrival stations must be different");
            return;
        }

        searchTrains(from, to, date);
    }

    /**
     * Search for trains matching the criteria
     */
    private void searchTrains(String from, String to, LocalDate date) {
        searchBtn.setDisable(true);
        searchBtn.setText("Searching...");

        CompletableFuture.runAsync(() -> {
            try {
                // In a real app, you'd resolve station names to IDs
                // For now, assuming station names map directly
                List<Schedule> schedules = scheduleService.fetchSchedules(1, 2, date);

                Platform.runLater(() -> {
                    ObservableList<Schedule> items = FXCollections.observableArrayList(schedules);
                    trainList.setItems(items);
                    searchBtn.setDisable(false);
                    searchBtn.setText("🔍 Search Trains");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    showError("Error searching trains: " + e.getMessage());
                    searchBtn.setDisable(false);
                    searchBtn.setText("🔍 Search Trains");
                });
            }
        });
    }

    /**
     * Toggle side panel visibility
     */
    @FXML
    public void toggleSidePanel() {
        if (sidePanel != null) {
            boolean visible = sidePanel.isVisible();
            sidePanel.setVisible(!visible);
            sidePanel.setManaged(!visible);

            if (fabMenu != null) {
                fabMenu.setText(visible ? "❮" : "❯");
            }
        }
    }

    /**
     * Navigate to my bookings page
     */
    @FXML
    public void openMyBookings() {
        // TODO: Navigate to bookings page
        System.out.println("Navigate to my bookings");
    }

    /**
     * Show error alert
     */
    private void showError(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
