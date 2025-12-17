package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.RouteDTO;
import me.ensah.trainLink.DTO.RouteDefinitionDTO;
import me.ensah.trainLink.model.Route;
import me.ensah.trainLink.model.RouteDefinition;
import me.ensah.trainLink.model.Station;
import me.ensah.trainLink.repository.RouteDefinitionRepository;
import me.ensah.trainLink.repository.RouteRepository;
import me.ensah.trainLink.repository.StationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RouteService {

    @Autowired
    private RouteRepository routeRepository;

    @Autowired
    private RouteDefinitionRepository routeDefinitionRepository;

    @Autowired
    private StationRepository stationRepository;

    @Transactional
    public RouteDTO createRoute(RouteDTO routeDTO) {
        Route route = new Route();
        route.setName(routeDTO.getName());
        route.setDescription(routeDTO.getDescription());

        Route savedRoute = routeRepository.save(route);

        List<RouteDefinition> definitions = new ArrayList<>();
        if (routeDTO.getDefinitions() != null) {
            for (RouteDefinitionDTO defDTO : routeDTO.getDefinitions()) {
                Station station = stationRepository.findById(defDTO.getStationId())
                        .orElseThrow(() -> new RuntimeException("Station not found with id: " + defDTO.getStationId()));

                RouteDefinition def = new RouteDefinition();
                def.setRoute(savedRoute);
                def.setStation(station);
                def.setStopOrder(defDTO.getStopOrder());
                def.setDistanceFromPrevKm(defDTO.getDistanceFromPrevKm());
                def.setStandardTravelTimeMins(defDTO.getStandardTravelTimeMins());

                definitions.add(def);
            }
            routeDefinitionRepository.saveAll(definitions);
        }

        return mapToDTO(savedRoute, definitions);
    }

    public List<RouteDTO> getAllRoutes() {
        List<Route> routes = routeRepository.findAll();
        return routes.stream().map(route -> {
            List<RouteDefinition> definitions = routeDefinitionRepository
                    .findByRouteIdOrderByStopOrderAsc(route.getId());
            return mapToDTO(route, definitions);
        }).collect(Collectors.toList());
    }

    public RouteDTO getRouteById(Long id) {
        Route route = routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        List<RouteDefinition> definitions = routeDefinitionRepository.findByRouteIdOrderByStopOrderAsc(id);
        return mapToDTO(route, definitions);
    }

    @Transactional
    public void deleteRoute(Long id) {
        routeRepository.deleteById(id);
    }

    private RouteDTO mapToDTO(Route route, List<RouteDefinition> definitions) {
        RouteDTO dto = new RouteDTO();
        dto.setId(route.getId());
        dto.setName(route.getName());
        dto.setDescription(route.getDescription());

        List<RouteDefinitionDTO> defDTOs = definitions.stream().map(def -> {
            RouteDefinitionDTO defDTO = new RouteDefinitionDTO();
            defDTO.setId(def.getId());
            defDTO.setStationId(def.getStation().getId());
            defDTO.setStationName(def.getStation().getName());
            defDTO.setStopOrder(def.getStopOrder());
            defDTO.setDistanceFromPrevKm(def.getDistanceFromPrevKm());
            defDTO.setStandardTravelTimeMins(def.getStandardTravelTimeMins());
            return defDTO;
        }).collect(Collectors.toList());

        dto.setDefinitions(defDTOs);
        return dto;
    }
}
