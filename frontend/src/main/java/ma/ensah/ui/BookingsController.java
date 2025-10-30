package ma.ensah.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import ma.ensah.config.Config;
import ma.ensah.config.Session;
import ma.ensah.model.BookingResponse;
import ma.ensah.model.BookingSummary;
import ma.ensah.net.ApiClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BookingsController {
    @FXML private ListView<BookingSummary> bookingList;
    @FXML private ListView<String> passengersList;
    @FXML private Label detailsTitle;
    @FXML private Label detailsMeta;
    @FXML private Label detailsStatus;
    @FXML private Button cancelBtn;

    private final ObservableList<BookingSummary> items = FXCollections.observableArrayList();
    private final ApiClient api = new ApiClient(Config.apiBaseUrl());
    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @FXML
    public void initialize() {
        if (!Session.isAuthenticated()) {
            info("Please log in to view your bookings.");
            goHome();
            return;
        }
    bookingList.setItems(items);
        bookingList.setPlaceholder(new Label("Loading..."));
        bookingList.setCellFactory(lv -> new ListCell<>() {
            private final Label ref = new Label();
            private final Label date = new Label();
            private final Label pax = new Label();
            private final Label amount = new Label();
            private final Label badge = new Label();
            private final HBox root;
            {
                ref.getStyleClass().add("booking-ref");
                date.getStyleClass().add("meta-muted");
                pax.getStyleClass().add("meta-muted");
                amount.getStyleClass().add("amount");
                badge.getStyleClass().addAll("badge");
                // Hard-set text colors to avoid JavaFX selected-cell default turning text white
                ref.setTextFill(Color.web("#111827"));
                amount.setTextFill(Color.web("#111827"));
                date.setTextFill(Color.web("#9aa3ae"));
                pax.setTextFill(Color.web("#9aa3ae"));
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                HBox meta = new HBox(10, date, pax);
                meta.setFillHeight(false);
                VBox left = new VBox(3, ref, meta);
                root = new HBox(12, left, spacer, amount, badge);
                root.getStyleClass().add("booking-item");
            }
            @Override
            protected void updateItem(BookingSummary item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    ref.setText(item.getReferenceCode());
                    LocalDateTime bdt = item.getBookingDate();
                    date.setText("Date: " + (bdt == null ? "-" : dtf.format(bdt)));
                    pax.setText("Passengers: " + item.getPassengersCount());
                    amount.setText(item.getTotalPrice() == null ? "" : (item.getTotalPrice().toString() + " MAD"));
                    // badge style
                    badge.getStyleClass().removeAll("badge-confirmed", "badge-cancelled");
                    String st = item.getStatus() == null ? "" : item.getStatus();
                    badge.setText(st);
                    if ("CONFIRMED".equalsIgnoreCase(st)) badge.getStyleClass().add("badge-confirmed");
                    else if ("CANCELLED".equalsIgnoreCase(st)) badge.getStyleClass().add("badge-cancelled");
                    setText(null);
                    setGraphic(root);
                }
            }
        });
        bookingList.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) loadDetails(sel.getReferenceCode());
        });
        passengersList.setCellFactory(lv -> new ListCell<>() {
            private final Label chip = new Label();
            private final HBox root;
            {
                chip.getStyleClass().add("chip");
                root = new HBox(chip);
                root.setSpacing(0);
                root.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                root.setPadding(new javafx.geometry.Insets(4, 6, 4, 6));
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); setText(null);} else { chip.setText(item); setGraphic(root); setText(null);} }
        });
        loadPage();
    }

    private void loadPage() {
        CompletableFuture.runAsync(() -> {
            try {
                Map<String,Object> page = api.get("/bookings?page=0&size=20", new TypeReference<Map<String,Object>>(){});
                @SuppressWarnings("unchecked") List<Map<String,Object>> content = (List<Map<String,Object>>) page.get("content");
                List<BookingSummary> data = content.stream().map(m -> {
                    BookingSummary s = new BookingSummary();
                    s.setBookingId(((Number)m.get("bookingId")).longValue());
                    s.setReferenceCode((String)m.get("referenceCode"));
                    s.setScheduleId(((Number)m.get("scheduleId")).longValue());
                    Object bd = m.get("bookingDate");
                    if (bd != null) {
                        try { s.setBookingDate(LocalDateTime.parse(bd.toString())); } catch (Exception ignore) { /* keep null */ }
                    }
                    s.setStatus((String)m.get("status"));
                    Object pc = m.get("passengersCount");
                    s.setPassengersCount(pc instanceof Number ? ((Number) pc).intValue() : 0);
                    Object tp = m.get("totalPrice");
                    if (tp != null) s.setTotalPrice(new java.math.BigDecimal(tp.toString()));
                    return s;
                }).collect(Collectors.toList());
                Platform.runLater(() -> {
                    items.setAll(data);
                    if (items.isEmpty()) {
                        bookingList.setPlaceholder(new Label("No bookings yet."));
                    } else {
                        bookingList.setPlaceholder(new Label(""));
                        bookingList.getSelectionModel().selectFirst();
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> error("Failed to load bookings: " + e.getMessage()));
            }
        });
    }

    private void loadDetails(String ref) {
        CompletableFuture.runAsync(() -> {
            try {
                BookingResponse res = api.get("/bookings/" + ref, new TypeReference<BookingResponse>(){});
                Platform.runLater(() -> showDetails(res));
            } catch (Exception e) {
                Platform.runLater(() -> error("Failed to fetch booking: " + e.getMessage()));
            }
        });
    }

    private void showDetails(BookingResponse res) {
        if (res == null) return;
        detailsTitle.setText("Booking " + res.getReferenceCode());
    String dateTxt = res.getBookingDate() == null ? "-" : dtf.format(res.getBookingDate());
        String totalTxt = res.getTotalPrice() == null ? "-" : res.getTotalPrice().toString() + " MAD";
        detailsMeta.setText(String.format("Date: %s • Total: %s", dateTxt, totalTxt));
        // status badge next to title
        detailsStatus.setText(res.getStatus() == null ? "" : res.getStatus());
        detailsStatus.getStyleClass().removeAll("badge", "badge-confirmed", "badge-cancelled");
        detailsStatus.getStyleClass().add("badge");
        if (res.getStatus() != null) {
            if ("CONFIRMED".equalsIgnoreCase(res.getStatus())) detailsStatus.getStyleClass().add("badge-confirmed");
            else if ("CANCELLED".equalsIgnoreCase(res.getStatus())) detailsStatus.getStyleClass().add("badge-cancelled");
        }
    passengersList.getItems().setAll(
        res.getPassengers() == null ? java.util.List.of() :
            res.getPassengers().stream().map(p -> p.getName() + " (" + p.getAge() + ")").collect(Collectors.toList())
    );
        boolean canCancel = res.getStatus() != null && res.getStatus().equalsIgnoreCase("CONFIRMED");
        // toggle style between secondary and danger
        cancelBtn.getStyleClass().removeAll("btn-danger", "btn-secondary");
        cancelBtn.getStyleClass().add(canCancel ? "btn-danger" : "btn-secondary");
        cancelBtn.setVisible(canCancel);
        cancelBtn.setManaged(canCancel);
        cancelBtn.setUserData(res.getReferenceCode());
    }

    @FXML public void cancelSelected() {
        Object ref = cancelBtn.getUserData();
        if (ref == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel booking " + ref + "?", ButtonType.NO, ButtonType.YES);
        confirm.setHeaderText(null);
        confirm.setTitle("Confirm");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                CompletableFuture.runAsync(() -> {
                    try {
                        api.delete("/bookings/" + ref);
                        Platform.runLater(() -> { info("Booking cancelled."); loadPage(); });
                    } catch (Exception e) {
                        Platform.runLater(() -> error("Failed to cancel: " + e.getMessage()));
                    }
                });
            }
        });
    }

    @FXML public void goHome() { Navigation.goTo((javafx.stage.Stage) bookingList.getScene().getWindow(), "/views/MainView.fxml"); }

    private void info(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Info");
        a.showAndWait();
    }
    private void error(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
        a.setHeaderText(null);
        a.setTitle("Error");
        a.showAndWait();
    }
}
