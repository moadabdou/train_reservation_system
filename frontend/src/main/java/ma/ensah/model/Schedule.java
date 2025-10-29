package ma.ensah.model;

import java.time.LocalDateTime;

public class Schedule {
    private long id;
    private String trainName;
    private String departureStationName;
    private String arrivalStationName;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private double price;
    private int availableSeats;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getTrainName() { return trainName; }
    public void setTrainName(String trainName) { this.trainName = trainName; }
    public String getDepartureStationName() { return departureStationName; }
    public void setDepartureStationName(String departureStationName) { this.departureStationName = departureStationName; }
    public String getArrivalStationName() { return arrivalStationName; }
    public void setArrivalStationName(String arrivalStationName) { this.arrivalStationName = arrivalStationName; }
    public LocalDateTime getDepartureTime() { return departureTime; }
    public void setDepartureTime(LocalDateTime departureTime) { this.departureTime = departureTime; }
    public LocalDateTime getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(LocalDateTime arrivalTime) { this.arrivalTime = arrivalTime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
