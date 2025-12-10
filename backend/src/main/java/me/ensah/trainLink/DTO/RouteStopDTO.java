package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouteStopDTO {
    private Long id;
    private StationDTO station;
    private LocalDateTime arrivalTime;
    private LocalDateTime departureTime;
    private Integer stopOrder;
}
