package me.ensah.trainLink.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import me.ensah.trainLink.DTO.StationDTO;
import me.ensah.trainLink.model.Station;
import me.ensah.trainLink.repository.StationRepository;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<StationDTO> getAllStations() {
        List<Station> stations = stationRepository.findAll();
        return stations.stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(s -> new StationDTO(s.getId(), s.getName(), s.getLatitude(), s.getLongitude(), s.getDescription()))
                .collect(Collectors.toList());
    }

    public StationDTO getStationInfo(Long id) {
        Station station = stationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return new StationDTO(station.getId(), station.getName(), station.getLatitude(), station.getLongitude(),
                station.getDescription());
    }
}
