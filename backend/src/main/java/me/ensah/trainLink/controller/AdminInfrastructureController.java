package me.ensah.trainLink.controller;

import me.ensah.trainLink.DTO.ProviderDTO;
import me.ensah.trainLink.DTO.StationDTO;
import me.ensah.trainLink.DTO.TrainDTO;
import me.ensah.trainLink.services.ProviderService;
import me.ensah.trainLink.services.StationService;
import me.ensah.trainLink.services.TrainService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
public class AdminInfrastructureController {

    private final ProviderService providerService;
    private final StationService stationService;
    private final TrainService trainService;

    public AdminInfrastructureController(ProviderService providerService, StationService stationService,
            TrainService trainService) {
        this.providerService = providerService;
        this.stationService = stationService;
        this.trainService = trainService;
    }

    // --- Providers ---

    @GetMapping("/providers")
    public ResponseEntity<List<ProviderDTO>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    @GetMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> getProviderById(@PathVariable Long id) {
        return ResponseEntity.ok(providerService.getProviderById(id));
    }

    @PostMapping("/providers")
    public ResponseEntity<ProviderDTO> createProvider(@RequestBody ProviderDTO providerDTO) {
        return ResponseEntity.ok(providerService.createProvider(providerDTO));
    }

    @PutMapping("/providers/{id}")
    public ResponseEntity<ProviderDTO> updateProvider(@PathVariable Long id, @RequestBody ProviderDTO providerDTO) {
        return ResponseEntity.ok(providerService.updateProvider(id, providerDTO));
    }

    @DeleteMapping("/providers/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }

    // --- Trains ---

    @GetMapping("/trains")
    public ResponseEntity<List<TrainDTO>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @GetMapping("/trains/{id}")
    public ResponseEntity<TrainDTO> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    @PostMapping("/trains")
    public ResponseEntity<TrainDTO> createTrain(@RequestBody TrainDTO trainDTO) {
        return ResponseEntity.ok(trainService.createTrain(trainDTO));
    }

    @PutMapping("/trains/{id}")
    public ResponseEntity<TrainDTO> updateTrain(@PathVariable Long id, @RequestBody TrainDTO trainDTO) {
        return ResponseEntity.ok(trainService.updateTrain(id, trainDTO));
    }

    @DeleteMapping("/trains/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }

    // --- Stations ---

    @GetMapping("/stations")
    public ResponseEntity<List<StationDTO>> getAllStations() {
        return ResponseEntity.ok(stationService.getAllStations());
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<StationDTO> getStationById(@PathVariable Long id) {
        return ResponseEntity.ok(stationService.getStationInfo(id));
    }

    @PostMapping("/stations")
    public ResponseEntity<StationDTO> createStation(@RequestBody StationDTO stationDTO) {
        return ResponseEntity.ok(stationService.createStation(stationDTO));
    }

    @PutMapping("/stations/{id}")
    public ResponseEntity<StationDTO> updateStation(@PathVariable Long id, @RequestBody StationDTO stationDTO) {
        return ResponseEntity.ok(stationService.updateStation(id, stationDTO));
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable Long id) {
        stationService.deleteStation(id);
        return ResponseEntity.noContent().build();
    }

    // --- File Upload ---

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            // Save the file locally (for simplicity in this slice)
            // In a real app, you'd use S3 or similar
            String uploadDir = "uploads/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Return the URL (assuming we serve static files from /uploads)
            // You might need to configure Spring to serve this directory
            return ResponseEntity.ok("/uploads/" + fileName);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to upload file");
        }
    }
}
