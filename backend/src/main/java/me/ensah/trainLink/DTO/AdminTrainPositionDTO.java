package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminTrainPositionDTO {
    private Long trainId;
    private String trainName;
    private String routeName;
    private Double latitude;
    private Double longitude;
    private String status;
    private String nextStationName;
}
