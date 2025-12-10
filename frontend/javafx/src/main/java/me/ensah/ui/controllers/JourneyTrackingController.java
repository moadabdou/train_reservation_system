package me.ensah.ui.controllers;

import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import me.ensah.model.RouteStop;
import me.ensah.model.TrainPosition;
import me.ensah.services.JourneyService;
import me.ensah.net.ApiClient;

import javafx.scene.control.Alert;
import netscape.javascript.JSObject;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.ensah.config.Config;

public class JourneyTrackingController {

    @FXML
    private ListView<String> timelineListView;

    @FXML
    private WebView mapWebView;

    private JourneyService journeyService;
    private Long scheduleId;
    private Timer timer;
    private WebEngine webEngine;
    private boolean mapLoaded = false;

    public void initialize() {
        journeyService = new JourneyService(new ApiClient(Config.apiBaseUrl()));
        webEngine = mapWebView.getEngine();

        // Load Leaflet Map
        String html = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.css\" />" +
                "    <script src=\"https://unpkg.com/leaflet@1.9.4/dist/leaflet.js\"></script>" +
                "    <style>body { margin: 0; padding: 0; } #map { height: 100vh; width: 100%; }</style>" +
                "</head>" +
                "<body>" +
                "    <div id=\"map\"></div>" +
                "    <script>" +
                "        var map = L.map('map').setView([34.0, -6.8], 6);" + // Default to Morocco
                "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {" +
                "            attribution: '© OpenStreetMap contributors'" +
                "        }).addTo(map);" +
                "        var trainMarker;" +
                "        var routeLine;" +
                "        var stationMarkers = [];" +
                "        function updateTrainPosition(lat, lng) {" +
                "            if (trainMarker) {" +
                "                trainMarker.setLatLng([lat, lng]);" +
                "            } else {" +
                "                var trainIcon = L.icon({" +
                "                    iconUrl: 'https://cdn-icons-png.flaticon.com/512/1995/1995470.png'," + // Simple
                                                                                                            // train
                                                                                                            // icon
                "                    iconSize: [32, 32]," +
                "                    iconAnchor: [16, 16]" +
                "                });" +
                "                trainMarker = L.marker([lat, lng], {icon: trainIcon}).addTo(map);" +
                "            }" +
                "            map.panTo([lat, lng]);" +
                "        }" +
                "        function drawRoute(points, stations) {" +
                "            if (routeLine) map.removeLayer(routeLine);" +
                "            routeLine = L.polyline(points, {color: 'blue'}).addTo(map);" +
                "            map.fitBounds(routeLine.getBounds());" +
                "            " +
                "            // Clear old station markers" +
                "            stationMarkers.forEach(m => map.removeLayer(m));" +
                "            stationMarkers = [];" +
                "            " +
                "            stations.forEach(s => {" +
                "                var m = L.marker([s.lat, s.lng]).addTo(map);" +
                "                m.bindTooltip(s.name);" +
                "                m.on('click', function() { javaController.showStationInfo(s.id); });" +
                "                stationMarkers.push(m);" +
                "            });" +
                "        }" +
                "    </script>" +
                "</body>" +
                "</html>";

        webEngine.loadContent(html);

        webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) webEngine.executeScript("window");
                window.setMember("javaController", this);
                mapLoaded = true;
                if (scheduleId != null) {
                    loadRoute();
                }
            }
        });
    }

    public void showStationInfo(Long stationId) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Station Info");
            alert.setHeaderText("Station ID: " + stationId);
            alert.setContentText("Fetching info..."); // In real app, fetch info from API
            alert.showAndWait();
        });
    }

    public void setScheduleId(Long scheduleId) {
        this.scheduleId = scheduleId;
        if (mapLoaded) {
            loadRoute();
        }
        startTracking();
    }

    private void loadRoute() {
        new Thread(() -> {
            try {
                List<RouteStop> stops = journeyService.getRoute(scheduleId);
                Platform.runLater(() -> {
                    updateTimeline(stops);
                    drawRouteOnMap(stops);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void updateTimeline(List<RouteStop> stops) {
        timelineListView.getItems().clear();
        for (RouteStop stop : stops) {
            String info = stop.getStation().getName();
            if (stop.getArrivalTime() != null) {
                info += " - Arr: " + stop.getArrivalTime().toLocalTime();
            }
            if (stop.getDepartureTime() != null) {
                info += " - Dep: " + stop.getDepartureTime().toLocalTime();
            }
            timelineListView.getItems().add(info);
        }
    }

    private void drawRouteOnMap(List<RouteStop> stops) {
        StringBuilder points = new StringBuilder("[");
        StringBuilder stations = new StringBuilder("[");

        for (int i = 0; i < stops.size(); i++) {
            RouteStop stop = stops.get(i);
            if (stop.getStation().getLatitude() != null && stop.getStation().getLongitude() != null) {
                points.append("[").append(stop.getStation().getLatitude()).append(",")
                        .append(stop.getStation().getLongitude()).append("]");

                stations.append("{lat:").append(stop.getStation().getLatitude())
                        .append(",lng:").append(stop.getStation().getLongitude())
                        .append(",name:'").append(stop.getStation().getName().replace("'", "\\'")).append("'")
                        .append(",id:").append(stop.getStation().getId())
                        .append("}");

                if (i < stops.size() - 1) {
                    points.append(",");
                    stations.append(",");
                }
            }
        }
        points.append("]");
        stations.append("]");
        webEngine.executeScript("drawRoute(" + points.toString() + ", " + stations.toString() + ")");
    }

    private void startTracking() {
        if (timer != null)
            timer.cancel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    TrainPosition pos = journeyService.getPosition(scheduleId);
                    Platform.runLater(() -> {
                        if (pos.getLatitude() != null && pos.getLongitude() != null) {
                            webEngine.executeScript(
                                    "updateTrainPosition(" + pos.getLatitude() + ", " + pos.getLongitude() + ")");
                        }
                        // Update notifications or status here
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 5000); // Update every 5 seconds
    }

    public void stopTracking() {
        if (timer != null)
            timer.cancel();
    }
}
