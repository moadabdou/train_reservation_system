package me.ensah.model;

public class TrainPosition {
    private Double latitude;
    private Double longitude;
    private String status;
    private String nextStationName;
    private Long nextStationId;
    private Integer estimatedArrivalMinutes;

    public TrainPosition() {
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNextStationName() {
        return nextStationName;
    }

    public void setNextStationName(String nextStationName) {
        this.nextStationName = nextStationName;
    }

    public Long getNextStationId() {
        return nextStationId;
    }

    public void setNextStationId(Long nextStationId) {
        this.nextStationId = nextStationId;
    }

    public Integer getEstimatedArrivalMinutes() {
        return estimatedArrivalMinutes;
    }

    public void setEstimatedArrivalMinutes(Integer estimatedArrivalMinutes) {
        this.estimatedArrivalMinutes = estimatedArrivalMinutes;
    }
}
