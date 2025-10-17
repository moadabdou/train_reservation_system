package com.ensah.train_reservation_system.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ensah.train_reservation_system.DTO.ScheduleDTO;
import com.ensah.train_reservation_system.services.ScheduleService;

import java.time.LocalDate;
import java.util.List;

@RestController // This combines @Controller and @ResponseBody, returning JSON by default
@RequestMapping("/api/schedules") // All methods in this class will start with this URL path
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping 
    public List<ScheduleDTO> searchSchedules(
            @RequestParam Long from, 
            @RequestParam Long to,  
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        
        return scheduleService.findSchedules(from, to, date);
    }
}