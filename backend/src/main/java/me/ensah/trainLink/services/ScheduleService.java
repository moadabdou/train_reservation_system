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

@Service // Marks this as a service component for Spring
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleDTO> findSchedules(Long departureStationId, Long arrivalStationId, LocalDate date) {

        LocalDateTime startOfDay = date.atStartOfDay(); 
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);   

        List<Schedule> schedules = scheduleRepository.findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
            departureStationId,
            arrivalStationId,
            startOfDay,
            endOfDay
        );

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