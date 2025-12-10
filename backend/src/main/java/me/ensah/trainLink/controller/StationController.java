package me.ensah.trainLink.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.ensah.trainLink.DTO.StationDTO;
import me.ensah.trainLink.services.StationService;

import org.springframework.web.bind.annotation.PathVariable;

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

    @GetMapping("/{id}/info")
    public StationDTO getStationInfo(@PathVariable Long id) {
        return stationService.getStationInfo(id);
    }
}
