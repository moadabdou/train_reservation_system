package com.ensah.train_reservation_system.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.ensah.train_reservation_system.DTO.StationDTO;
import com.ensah.train_reservation_system.model.Station;
import com.ensah.train_reservation_system.repository.StationRepository;

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
                       .map(s -> new StationDTO(s.getId(), s.getName()))
                       .collect(Collectors.toList());
    }
}
