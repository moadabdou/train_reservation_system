package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.AdminTrainPositionDTO;
import me.ensah.trainLink.DTO.DashboardStatsDTO;
import me.ensah.trainLink.services.AdminDashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminDashboardController {

    @Autowired
    private AdminDashboardService adminDashboardService;

    @GetMapping("/dashboard-stats")
    public ResponseEntity<DashboardStatsDTO> getDashboardStats() {
        return ResponseEntity.ok(adminDashboardService.getDashboardStats());
    }

    @GetMapping("/live-map")
    public ResponseEntity<List<AdminTrainPositionDTO>> getLiveMap() {
        return ResponseEntity.ok(adminDashboardService.getLiveTrains());
    }
}
