package me.ensah.trainLink.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import me.ensah.trainLink.DTO.ScheduleDTO;
import me.ensah.trainLink.services.ScheduleService;

import java.time.LocalDate;
import java.util.List;

import me.ensah.trainLink.DTO.RouteStopDTO;
import org.springframework.web.bind.annotation.PathVariable;

@RestController // This combines @Controller and @ResponseBody, returning JSON by default
@RequestMapping("/api/schedules") // All methods in this class will start with this URL path
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping("/{id}/route")
    public List<RouteStopDTO> getRoute(@PathVariable Long id) {
        return scheduleService.getRouteStops(id);
    }

    @GetMapping
    public List<ScheduleDTO> searchSchedules(
            @RequestParam Long from,
            @RequestParam Long to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return scheduleService.findSchedules(from, to, date);
    }

    @GetMapping("/{id}/position")
    public me.ensah.trainLink.DTO.TrainPositionDTO getPosition(@PathVariable Long id) {
        return scheduleService.getTrainPosition(id);
    }
}