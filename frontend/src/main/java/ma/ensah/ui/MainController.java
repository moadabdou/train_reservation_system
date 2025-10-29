package ma.ensah.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import ma.ensah.config.Config;
import ma.ensah.config.Session;
import ma.ensah.model.Schedule;
import ma.ensah.model.Station;
import ma.ensah.net.ApiClient;
import ma.ensah.services.ScheduleService;
import ma.ensah.services.StationService;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    @FXML private Button headerLoginBtn;
    @FXML private Button headerRegisterBtn;
    @FXML private Button headerLogoutBtn;

    private StationService stationService;
    private ScheduleService scheduleService;
    private final ObservableList<Schedule> data = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        ApiClient api = new ApiClient(Config.apiBaseUrl());
        this.stationService = new StationService(api);
        this.scheduleService = new ScheduleService(api);

        // Setup train list with custom cells
        trainList.setItems(data);
        trainList.setCellFactory(listView -> new TrainCardCell());

        // Defaults
        datePicker.setValue(LocalDate.now());

        // Load stations async
        CompletableFuture.runAsync(() -> {
            try {
                List<Station> stations = stationService.fetchStations();
                Platform.runLater(() -> {
                    fromStation.setItems(FXCollections.observableArrayList(stations));
                    toStation.setItems(FXCollections.observableArrayList(stations));
                });
            } catch (Exception e) {
                showError("Failed to load stations: " + e.getMessage());
            }
        });

        updateHeaderButtons();
    }

    @FXML
    public void onSearch() {
        Station from = fromStation.getValue();
        Station to = toStation.getValue();
        LocalDate date = datePicker.getValue();

        if (from == null || to == null || date == null) {
            showError("Please select departure, arrival stations and date.");
            return;
        }

        if (from.getId() == to.getId()) {
            showError("Departure and arrival stations must be different.");
            return;
        }

        searchBtn.setDisable(true);
        searchBtn.setText("Searching...");
        data.clear();

        CompletableFuture.runAsync(() -> {
            try {
                List<Schedule> schedules = scheduleService.fetchSchedules(from.getId(), to.getId(), date);
                Platform.runLater(() -> {
                    data.setAll(schedules);
                    searchBtn.setDisable(false);
                    searchBtn.setText("🔍 Search Trains");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    searchBtn.setDisable(false);
                    searchBtn.setText("🔍 Search Trains");
                    showError("Search failed: " + e.getMessage());
                });
            }
        });
    }

    private void showError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        alert.setHeaderText(null);
        alert.setTitle("Error");
        alert.showAndWait();
    }

    @FXML
    public void goLogin() {
        Navigation.goTo((javafx.stage.Stage) searchBtn.getScene().getWindow(), "/views/LoginView.fxml");
    }

    @FXML
    public void goRegister() {
        Navigation.goTo((javafx.stage.Stage) searchBtn.getScene().getWindow(), "/views/RegisterView.fxml");
    }

    @FXML
    public void goHome() {
        // Already on home; keep for consistency
        // Could refresh here if needed
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
}
