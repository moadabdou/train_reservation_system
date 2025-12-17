package me.ensah.trainLink.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class FinancialStatsDTO {
    private BigDecimal totalRevenue;
    private BigDecimal totalRefunds;
    private Map<String, BigDecimal> revenueByRoute;
}
