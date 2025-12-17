package me.ensah.trainLink.DTO;

import lombok.Data;

@Data
public class RouteDefinitionDTO {
    private Long id;
    private Long stationId;
    private String stationName;
    private Integer stopOrder;
    private Double distanceFromPrevKm;
    private Integer standardTravelTimeMins;
}
