package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TrainPositionDTO {
    private Double latitude;
    private Double longitude;
    private String status; // "AT_STATION", "MOVING", "NOT_STARTED", "ARRIVED"
    private String nextStationName;
    private Long nextStationId;
    private Integer estimatedArrivalMinutes;
}
