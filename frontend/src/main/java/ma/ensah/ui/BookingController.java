package ma.ensah.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import ma.ensah.config.Session;
import ma.ensah.model.*;
import ma.ensah.services.BookingService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BookingController {
    @FXML private Label scheduleRouteLabel;
    @FXML private Label scheduleMetaLabel;
    @FXML private VBox passengerList;
    @FXML private Label passengerCountLabel;
    @FXML private Label totalPriceLabel;
    @FXML private Button confirmBtn;

    private final ObservableList<Passenger> passengers = FXCollections.observableArrayList();
    private final BookingService bookingService = BookingService.defaultInstance();

    @FXML
    public void initialize() {
        if (!Session.isAuthenticated()) {
            info("Please log in to book.");
            goLogin();
            return;
        }

        Schedule s = Session.getSelectedSchedule();
        if (s == null) {
            warn("No schedule selected. Please choose a train first.");
            goHome();
            return;
        }
    String route = String.format("%s  |  %s → %s", s.getTrainName(), s.getDepartureStationName(), s.getArrivalStationName());
    String dep = s.getDepartureTime() != null ? s.getDepartureTime().toString() : "";
    String arr = s.getArrivalTime() != null ? s.getArrivalTime().toString() : "";
    String dur = "";
    if (s.getDepartureTime() != null && s.getArrivalTime() != null) {
        java.time.Duration d = java.time.Duration.between(s.getDepartureTime(), s.getArrivalTime());
        long h = d.toHours();
        long m = d.toMinutesPart();
        dur = String.format(" • Duration: %dh %dm", h, m);
    }
    scheduleRouteLabel.setText(route);
    scheduleMetaLabel.setText(String.format("%s - %s • Price: %.0f MAD%s", dep, arr, s.getPrice(), dur));

        // Keep UI in sync with list changes
        passengers.addListener((ListChangeListener<Passenger>) c -> renderPassengers());

        // Start with one passenger card
        passengers.add(new Passenger("", 18));
        updateSummary();
    }

    @FXML
    public void addPassenger() {
        passengers.add(new Passenger("", 18));
        updateSummary();
    }

    private void removePassenger(Passenger p) {
        passengers.remove(p);
        updateSummary();
    }

    private void renderPassengers() {
        passengerList.getChildren().clear();
        for (Passenger p : passengers) {
            int index = passengers.indexOf(p) + 1;
            passengerList.getChildren().add(buildPassengerCard(p, index));
        }
    }

    private VBox buildPassengerCard(Passenger p, int index) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("search-card", "passenger-card");
        card.setPadding(new Insets(12));

        // Top bar: title + remove
        HBox top = new HBox(10);
        Label title = new Label("Passenger " + index);
        title.getStyleClass().add("section-title");
        Region topSpacer = new Region();
        HBox.setHgrow(topSpacer, Priority.ALWAYS);
        Button removeBtnTop = new Button("Remove");
        removeBtnTop.getStyleClass().add("btn-secondary");
        removeBtnTop.setOnAction(e -> removePassenger(p));
        removeBtnTop.setDisable(passengers.size() <= 1);
        top.getChildren().addAll(title, topSpacer, removeBtnTop);

        HBox row = new HBox(10);
        row.setFillHeight(true);

        VBox nameBox = new VBox(6);
        Label nameLbl = new Label("Full name");
        nameLbl.getStyleClass().add("input-label");
        TextField nameField = new TextField();
        nameField.getStyleClass().add("text-input");
        nameField.setPromptText("e.g. Alice Doe");
        nameField.setText(p.getName() == null ? "" : p.getName());
        nameField.textProperty().addListener((obs, ov, nv) -> { p.setName(nv); updateSummary(); });
        nameBox.getChildren().addAll(nameLbl, nameField);
        HBox.setHgrow(nameBox, Priority.ALWAYS);

        VBox ageBox = new VBox(6);
        Label ageLbl = new Label("Age");
        ageLbl.getStyleClass().add("input-label");
        javafx.scene.control.Spinner<Integer> ageSpinner = new javafx.scene.control.Spinner<>(0, 120, Math.max(0, p.getAge()), 1);
        ageSpinner.setEditable(true);
        ageSpinner.setPrefWidth(120);
        ageSpinner.valueProperty().addListener((obs, ov, nv) -> { p.setAge(nv == null ? 0 : nv); });
        ageBox.getChildren().addAll(ageLbl, ageSpinner);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        row.getChildren().addAll(nameBox, ageBox, spacer);
        card.getChildren().addAll(top, row);
        return card;
    }

    @FXML
    public void confirmBooking() {
        if (!Session.isAuthenticated()) { info("Please log in to book."); goLogin(); return; }
        Schedule s = Session.getSelectedSchedule();
        if (s == null) { warn("No schedule selected."); goHome(); return; }

        List<Passenger> payload = new ArrayList<>();
        for (Passenger p : passengers) {
            String name = p.getName() == null ? "" : p.getName().trim();
            int age = p.getAge();
            if (!name.isBlank() && age >= 0) {
                payload.add(new Passenger(name, age));
            }
        }
        if (payload.isEmpty()) {
            warn("Please add at least one passenger with a valid name and age.");
            return;
        }

        // Optional local guard against overbooking (backend also handles atomically)
        int seats = s.getAvailableSeats();
        if (seats > 0 && payload.size() > seats) {
            warn("Only " + seats + " seats remaining for this schedule.");
            return;
        }

        confirmBtn.setDisable(true);
        confirmBtn.setText("Booking...");

        BookingRequest req = new BookingRequest(s.getId(), payload);
        CompletableFuture.runAsync(() -> {
            try {
                BookingResponse res = bookingService.createBooking(req);
                Platform.runLater(() -> {
                    confirmBtn.setDisable(false);
                    confirmBtn.setText("Confirm Booking");
                    success("Booking confirmed! Reference: " + res.getReferenceCode());
                    goHome();
                });
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> {
                    confirmBtn.setDisable(false);
                    confirmBtn.setText("Confirm Booking");
                    error("Booking failed: " + e.getMessage());
                });
            }
        });
    }

    private void updateSummary() {
        Schedule s = Session.getSelectedSchedule();
        if (s == null) return;
        int count = (int) passengers.stream()
                .filter(p -> p.getName() != null && !p.getName().isBlank())
                .count();
        passengerCountLabel.setText(count + " passenger" + (count == 1 ? "" : "s"));
        double price = s.getPrice() * Math.max(count, 1);
        totalPriceLabel.setText(String.format("%.0f MAD", price));
    }

    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Error");
        a.showAndWait();
    }
    private void warn(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Warning");
        a.showAndWait();
    }
    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Info");
        a.showAndWait();
    }
    private void success(String msg) { info(msg); }

    @FXML public void goBack() { goHome(); }
    @FXML public void goHome() {
        javafx.stage.Stage stage = currentStage();
        if (stage != null) Navigation.goTo(stage, "/views/MainView.fxml");
        else Platform.runLater(this::goHome);
    }
    private void goLogin() {
        javafx.stage.Stage stage = currentStage();
        if (stage != null) Navigation.goTo(stage, "/views/LoginView.fxml");
        else Platform.runLater(this::goLogin);
    }

    private javafx.stage.Stage currentStage() {
        if (confirmBtn != null && confirmBtn.getScene() != null) {
            return (javafx.stage.Stage) confirmBtn.getScene().getWindow();
        }
        return javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .findFirst()
                .map(w -> (javafx.stage.Stage) w)
                .orElse(null);
    }
}
