package me.ensah.trainLink.services;

import org.springframework.stereotype.Service;

import me.ensah.trainLink.DTO.ScheduleDTO;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.repository.ScheduleRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import me.ensah.trainLink.DTO.TrainPositionDTO;
import me.ensah.trainLink.DTO.RouteStopDTO;
import me.ensah.trainLink.DTO.StationDTO;
import me.ensah.trainLink.model.RouteStop;
import me.ensah.trainLink.repository.RouteStopRepository;
import java.time.Duration;

@Service // Marks this as a service component for Spring
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final RouteStopRepository routeStopRepository;

    public ScheduleService(ScheduleRepository scheduleRepository, RouteStopRepository routeStopRepository) {
        this.scheduleRepository = scheduleRepository;
        this.routeStopRepository = routeStopRepository;
    }

    public TrainPositionDTO getTrainPosition(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        LocalDateTime now = LocalDateTime.now();

        // Case 1: Not started
        if (now.isBefore(schedule.getDepartureTime())) {
            return new TrainPositionDTO(
                    schedule.getDepartureStation().getLatitude(),
                    schedule.getDepartureStation().getLongitude(),
                    "NOT_STARTED",
                    schedule.getDepartureStation().getName(),
                    schedule.getDepartureStation().getId(),
                    (int) Duration.between(now, schedule.getDepartureTime()).toMinutes());
        }

        // Case 2: Arrived
        if (now.isAfter(schedule.getArrivalTime())) {
            return new TrainPositionDTO(
                    schedule.getArrivalStation().getLatitude(),
                    schedule.getArrivalStation().getLongitude(),
                    "ARRIVED",
                    null,
                    null,
                    0);
        }

        // Case 3: En route
        // Construct full list of stops: Start -> Intermediates -> End
        List<RouteStop> intermediateStops = routeStopRepository.findByScheduleIdOrderByStopOrderAsc(scheduleId);

        // We need to find which segment the train is in.
        // Segments:
        // 1. Start -> First Stop
        // 2. Stop i -> Stop i+1
        // 3. Last Stop -> End

        // Let's generalize: Point A -> Point B

        // Current segment start
        LocalDateTime segmentStartTime = schedule.getDepartureTime();
        Double startLat = schedule.getDepartureStation().getLatitude();
        Double startLng = schedule.getDepartureStation().getLongitude();

        // Iterate through stops to find the current segment
        for (RouteStop stop : intermediateStops) {
            if (now.isBefore(stop.getArrivalTime())) {
                // Train is between previous point and this stop
                return calculateInterpolatedPosition(
                        startLat, startLng, segmentStartTime,
                        stop.getStation().getLatitude(), stop.getStation().getLongitude(), stop.getArrivalTime(),
                        now,
                        stop.getStation().getName(),
                        stop.getStation().getId());
            } else if (now.isBefore(stop.getDepartureTime())) {
                // Train is AT this station
                return new TrainPositionDTO(
                        stop.getStation().getLatitude(),
                        stop.getStation().getLongitude(),
                        "AT_STATION",
                        stop.getStation().getName(),
                        stop.getStation().getId(),
                        0);
            }

            // Move to next segment
            segmentStartTime = stop.getDepartureTime();
            startLat = stop.getStation().getLatitude();
            startLng = stop.getStation().getLongitude();
        }

        // If we are here, we are between last stop (or start) and End
        return calculateInterpolatedPosition(
                startLat, startLng, segmentStartTime,
                schedule.getArrivalStation().getLatitude(), schedule.getArrivalStation().getLongitude(),
                schedule.getArrivalTime(),
                now,
                schedule.getArrivalStation().getName(),
                schedule.getArrivalStation().getId());
    }

    private TrainPositionDTO calculateInterpolatedPosition(
            Double lat1, Double lng1, LocalDateTime t1,
            Double lat2, Double lng2, LocalDateTime t2,
            LocalDateTime now,
            String nextStationName, Long nextStationId) {

        long totalSeconds = Duration.between(t1, t2).getSeconds();
        long elapsedSeconds = Duration.between(t1, now).getSeconds();

        if (totalSeconds == 0)
            totalSeconds = 1; // Avoid division by zero

        double fraction = (double) elapsedSeconds / totalSeconds;

        Double currentLat = lat1 + (lat2 - lat1) * fraction;
        Double currentLng = lng1 + (lng2 - lng1) * fraction;

        int minutesToNext = (int) Duration.between(now, t2).toMinutes();

        return new TrainPositionDTO(
                currentLat,
                currentLng,
                "MOVING",
                nextStationName,
                nextStationId,
                minutesToNext);
    }

    public List<RouteStopDTO> getRouteStops(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Schedule not found"));

        List<RouteStop> stops = routeStopRepository.findByScheduleIdOrderByStopOrderAsc(scheduleId);
        java.util.List<RouteStopDTO> routeStopDTOs = new java.util.ArrayList<>();

        // Add Departure Station
        StationDTO depStationDto = new StationDTO(
                schedule.getDepartureStation().getId(),
                schedule.getDepartureStation().getName(),
                schedule.getDepartureStation().getLatitude(),
                schedule.getDepartureStation().getLongitude(),
                schedule.getDepartureStation().getDescription());

        RouteStopDTO depStop = new RouteStopDTO(
                -1L, // Virtual ID
                depStationDto,
                schedule.getDepartureTime(), // Arrival at start is same as departure
                schedule.getDepartureTime(),
                0);
        routeStopDTOs.add(depStop);

        // Add Intermediate Stops
        routeStopDTOs.addAll(stops.stream().map(this::convertToRouteStopDto).collect(Collectors.toList()));

        // Add Arrival Station
        StationDTO arrStationDto = new StationDTO(
                schedule.getArrivalStation().getId(),
                schedule.getArrivalStation().getName(),
                schedule.getArrivalStation().getLatitude(),
                schedule.getArrivalStation().getLongitude(),
                schedule.getArrivalStation().getDescription());

        RouteStopDTO arrStop = new RouteStopDTO(
                -2L, // Virtual ID
                arrStationDto,
                schedule.getArrivalTime(),
                schedule.getArrivalTime(), // Departure from end is same as arrival
                routeStopDTOs.size()); // Next order
        routeStopDTOs.add(arrStop);

        return routeStopDTOs;
    }

    private RouteStopDTO convertToRouteStopDto(RouteStop stop) {
        StationDTO stationDto = new StationDTO(
                stop.getStation().getId(),
                stop.getStation().getName(),
                stop.getStation().getLatitude(),
                stop.getStation().getLongitude(),
                stop.getStation().getDescription());
        return new RouteStopDTO(
                stop.getId(),
                stationDto,
                stop.getArrivalTime(),
                stop.getDepartureTime(),
                stop.getStopOrder());
    }

    public List<ScheduleDTO> findSchedules(Long departureStationId, Long arrivalStationId, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<Schedule> schedules = scheduleRepository
                .findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
                        departureStationId,
                        arrivalStationId,
                        startOfDay,
                        endOfDay);

        return schedules.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private ScheduleDTO convertToDto(Schedule schedule) {

        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDepartureTime(schedule.getDepartureTime());
        dto.setArrivalTime(schedule.getArrivalTime());
        dto.setPrice(schedule.getPrice());
        dto.setAvailableSeats(schedule.getAvailableSeats());
        dto.setTrainName(schedule.getTrain().getName());
        dto.setDepartureStationName(schedule.getDepartureStation().getName());
        dto.setArrivalStationName(schedule.getArrivalStation().getName());

        return dto;
    }
}