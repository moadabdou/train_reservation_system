package me.ensah.trainLink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.ensah.trainLink.DTO.ScheduleDTO;
import me.ensah.trainLink.DTO.ScheduleGenerationRequest;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.services.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/api/admin/schedules")
public class AdminScheduleController {

    private final ScheduleService scheduleService;

    public AdminScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public ResponseEntity<List<ScheduleDTO>> getAllSchedules() {
        return ResponseEntity.ok(scheduleService.getAllSchedules());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate")
    public ResponseEntity<Schedule> generateSchedule(@RequestBody ScheduleGenerationRequest request) {
        Schedule schedule = scheduleService.generateSchedule(
                request.getRouteId(),
                request.getTrainId(),
                request.getStartTime(),
                request.getBasePrice(),
                request.isIncludeIntermediateStops());
        return ResponseEntity.ok(schedule);
    }
}
