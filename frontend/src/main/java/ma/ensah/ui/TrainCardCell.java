package ma.ensah.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import ma.ensah.config.Session;
import ma.ensah.model.Schedule;

import java.time.Duration;
import java.time.format.DateTimeFormatter;

public class TrainCardCell extends ListCell<Schedule> {
    private final VBox card = new VBox();
    private final HBox header = new HBox();
    private final HBox body = new HBox();
    private final HBox footer = new HBox();

    private final Label trainName = new Label();
    private final Label price = new Label();
    private final Label depStation = new Label();
    private final Label depTime = new Label();
    private final Label arrStation = new Label();
    private final Label arrTime = new Label();
    private final Label duration = new Label();
    private final Label seats = new Label();
    private final Button bookBtn = new Button("Proceed to booking");

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    public TrainCardCell() {
        setupUI();
        // Bind card width to the ListCell width (minus a small inset) so it never
        // extends under the vertical scrollbar and avoids triggering horizontal scroll.
        card.prefWidthProperty().bind(widthProperty().subtract(16));
    }

    private void setupUI() {
        card.getStyleClass().add("train-card");
        card.setSpacing(14);
        card.setPadding(new Insets(20));
        card.setMaxWidth(Double.MAX_VALUE);

        // Header: Train name and price
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(8);
        HBox.setHgrow(header, Priority.ALWAYS);

        trainName.getStyleClass().add("train-name");
        trainName.setFont(Font.font("System", FontWeight.BOLD, 15));

        Label trainIcon = new Label("🚆");
        trainIcon.setStyle("-fx-font-size: 16px; -fx-text-fill: #6b7280;");
        trainIcon.getStyleClass().add("info-text");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        price.getStyleClass().add("train-price");
        price.setFont(Font.font("System", FontWeight.BOLD, 18));

        header.getChildren().addAll(trainIcon, trainName, spacer, price);

        // Body: Departure and Arrival info
        body.setSpacing(30);
        body.setAlignment(Pos.CENTER_LEFT);

        VBox depBox = new VBox(5);
        Label depLabel = new Label("📍 Departure");
        depLabel.getStyleClass().add("journey-label");
        depStation.getStyleClass().add("station-name");
        depStation.setFont(Font.font("System", FontWeight.BOLD, 14));
        depTime.getStyleClass().add("journey-time");
        depBox.getChildren().addAll(depLabel, depStation, depTime);

        VBox arrBox = new VBox(5);
        Label arrLabel = new Label("📍 Destination");
        arrLabel.getStyleClass().add("journey-label");
        arrStation.getStyleClass().add("station-name");
        arrStation.setFont(Font.font("System", FontWeight.BOLD, 14));
        arrTime.getStyleClass().add("journey-time");
        arrBox.getChildren().addAll(arrLabel, arrStation, arrTime);

        body.getChildren().addAll(depBox, arrBox);

        // Footer: Duration, seats, and book button
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setSpacing(16);

        HBox durationBox = new HBox(5);
        durationBox.setAlignment(Pos.CENTER_LEFT);
        durationBox.setMinWidth(Region.USE_PREF_SIZE);
        HBox.setHgrow(durationBox, Priority.NEVER);
        Label durationIcon = new Label("⏱");
        durationIcon.setStyle("-fx-font-size: 13px;");
        durationIcon.getStyleClass().add("info-text");
        duration.getStyleClass().add("info-text");
        duration.setWrapText(true);
        duration.setMinWidth(Region.USE_PREF_SIZE);
        Label durationText = new Label("Duration:");
        durationText.getStyleClass().add("info-text");
        durationText.setPrefWidth(90);
        durationText.setMinWidth(Region.USE_PREF_SIZE);
        durationBox.getChildren().addAll(durationIcon, durationText, duration);

        HBox seatsBox = new HBox(5);
        seatsBox.setAlignment(Pos.CENTER_LEFT);
        seatsBox.setMinWidth(Region.USE_PREF_SIZE);
        HBox.setHgrow(seatsBox, Priority.NEVER);
        Label seatsIcon = new Label("👥");
        seatsIcon.setStyle("-fx-font-size: 13px;");
        seatsIcon.getStyleClass().add("info-text");
        seats.getStyleClass().add("seats-available");
        seats.setWrapText(true);
        seats.setMinWidth(Region.USE_PREF_SIZE);
        Label seatsText = new Label("Seats remaining:");
        seatsText.getStyleClass().add("info-text");
        seatsText.setPrefWidth(90);
        seatsText.setMinWidth(Region.USE_PREF_SIZE);
        seatsBox.getChildren().addAll(seatsIcon, seatsText, seats);

        // Left info as a FlowPane to wrap on smaller widths
        FlowPane leftInfo = new FlowPane();
        leftInfo.setHgap(20);
        leftInfo.setVgap(6);
        leftInfo.getChildren().addAll(durationBox, seatsBox);
        leftInfo.setPrefWrapLength(400);
        HBox.setHgrow(leftInfo, Priority.ALWAYS);

        bookBtn.getStyleClass().add("btn-book");
        bookBtn.setPrefWidth(280);
        bookBtn.setPrefHeight(32);
    bookBtn.setOnAction(e -> onProceedToBooking());

        footer.getChildren().clear();
        footer.getChildren().addAll(leftInfo, bookBtn);

        card.getChildren().addAll(header, new Separator(), body, footer);
    }

    @Override
    protected void updateItem(Schedule schedule, boolean empty) {
        super.updateItem(schedule, empty);

        if (empty || schedule == null) {
            setGraphic(null);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setMinHeight(Region.USE_COMPUTED_SIZE);
            setPrefHeight(Region.USE_COMPUTED_SIZE);
            setMaxHeight(Region.USE_COMPUTED_SIZE);
        } else {
            trainName.setText(schedule.getTrainName());
            price.setText(String.format("%.0f MAD", schedule.getPrice()));

            depStation.setText(schedule.getDepartureStationName());
            depTime.setText(schedule.getDepartureTime() != null ? TIME_FMT.format(schedule.getDepartureTime()) : "");

            arrStation.setText(schedule.getArrivalStationName());
            arrTime.setText(schedule.getArrivalTime() != null ? TIME_FMT.format(schedule.getArrivalTime()) : "");

            if (schedule.getDepartureTime() != null && schedule.getArrivalTime() != null) {
                Duration dur = Duration.between(schedule.getDepartureTime(), schedule.getArrivalTime());
                long hours = dur.toHours();
                long mins = dur.toMinutesPart();
                duration.setText(String.format("%dh %dm", hours, mins));
            } else {
                duration.setText("");
            }

            seats.setText(String.valueOf(schedule.getAvailableSeats()));

            // Attach card; allow JavaFX to compute heights
            setGraphic(card);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            setMinHeight(Region.USE_COMPUTED_SIZE);
            setPrefHeight(Region.USE_COMPUTED_SIZE);
            setMaxHeight(Region.USE_COMPUTED_SIZE);
        }
    }

    private void onProceedToBooking() {
        Schedule schedule = getItem();
        if (schedule == null) return;

        if (!Session.isAuthenticated()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Login required");
            alert.setHeaderText(null);
            alert.setContentText("Please log in to proceed with booking.");
            alert.showAndWait();
            // Navigate to login view
            Stage stage = (Stage) card.getScene().getWindow();
            Navigation.goTo(stage, "/views/LoginView.fxml");
            return;
        }

        // Store selected schedule and navigate to booking page
        Session.setSelectedSchedule(schedule);
        Stage stage = (Stage) card.getScene().getWindow();
        Navigation.goTo(stage, "/views/BookingView.fxml");
    }
}