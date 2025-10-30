package ma.ensah.model;

import java.util.List;

public class BookingRequest {
    private long scheduleId;
    private List<Passenger> passengers;

    public BookingRequest() {}
    public BookingRequest(long scheduleId, List<Passenger> passengers) {
        this.scheduleId = scheduleId;
        this.passengers = passengers;
    }

    public long getScheduleId() { return scheduleId; }
    public void setScheduleId(long scheduleId) { this.scheduleId = scheduleId; }
    public List<Passenger> getPassengers() { return passengers; }
    public void setPassengers(List<Passenger> passengers) { this.passengers = passengers; }
}
