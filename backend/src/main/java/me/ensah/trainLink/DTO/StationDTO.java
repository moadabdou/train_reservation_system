package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StationDTO {
    private Long id;
    private String name;
    private Double latitude;
    private Double longitude;
    private String description;
}
