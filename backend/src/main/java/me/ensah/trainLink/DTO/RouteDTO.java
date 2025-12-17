package me.ensah.trainLink.DTO;

import lombok.Data;
import java.util.List;

@Data
public class RouteDTO {
    private Long id;
    private String name;
    private String description;
    private List<RouteDefinitionDTO> definitions;
}
