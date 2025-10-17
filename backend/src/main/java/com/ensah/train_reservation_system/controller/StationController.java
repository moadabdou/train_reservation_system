package com.ensah.train_reservation_system.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ensah.train_reservation_system.DTO.StationDTO;
import com.ensah.train_reservation_system.services.StationService;

@RestController
@RequestMapping("/api/stations")
public class StationController {

    private final StationService stationService;

    public StationController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping
    public List<StationDTO> getStations() {
        return stationService.getAllStations();
    }
}
