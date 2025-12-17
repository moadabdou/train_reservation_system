package me.ensah.trainLink.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScheduleGenerationRequest {
    private Long routeId;
    private Long trainId;
    private LocalDateTime startTime;
    private BigDecimal basePrice;
    private boolean includeIntermediateStops = true;
}
