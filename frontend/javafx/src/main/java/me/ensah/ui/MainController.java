package me.ensah.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import me.ensah.config.Config;
import me.ensah.model.Schedule;
import me.ensah.model.Station;
import me.ensah.net.ApiClient;
import me.ensah.services.ScheduleService;
import me.ensah.services.StationService;
import me.ensah.ui.controllers.HeaderController;
import me.ensah.ui.controllers.SidebarController;

import java.time.LocalDate;
import java.util.List;

public class MainController {

    @FXML
    private ComboBox<Station> fromStation;
    @FXML
    private ComboBox<Station> toStation;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button searchBtn;
    @FXML
    private ListView<Schedule> trainList;

    @FXML
    private HeaderController headerController;
    @FXML
    private SidebarController sidebarController;

    private final ApiClient api = new ApiClient(Config.apiBaseUrl());
    private final StationService stationService = new StationService(api);
    private final ScheduleService scheduleService = new ScheduleService(api);

    @FXML
    public void initialize() {
        // Initialize date picker to today
        datePicker.setValue(LocalDate.now());

        // Set custom cell factory for train list
        trainList.setCellFactory(param -> new TrainCardCell());

        // Load stations
        loadStations();

        // Connect header and sidebar controllers
        if (headerController != null && sidebarController != null) {
            headerController.setSidebarController(sidebarController);
        }
    }

    private void loadStations() {
        try {
            List<Station> stations = stationService.fetchStations();
            fromStation.getItems().setAll(stations);
            toStation.getItems().setAll(stations);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load stations: " + e.getMessage());
        }
    }

    @FXML
    private void onSearch(ActionEvent event) {
        Station from = fromStation.getValue();
        Station to = toStation.getValue();
        LocalDate date = datePicker.getValue();

        if (from == null || to == null || date == null) {
            showAlert("Validation Error", "Please select departure, arrival and date.");
            return;
        }

        try {
            List<Schedule> schedules = scheduleService.fetchSchedules(from.getId(), to.getId(), date);
            trainList.getItems().setAll(schedules);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to search schedules: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
