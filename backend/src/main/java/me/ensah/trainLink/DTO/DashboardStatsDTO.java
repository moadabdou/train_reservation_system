package me.ensah.trainLink.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class DashboardStatsDTO {
    private long totalUsers;
    private long activeTrains;
    private long bookingsToday;
    private BigDecimal revenueToday;
    private double occupancyRate;
    private BigDecimal totalRevenue;
    private double cancellationRate;
    private String topRoute;
    private List<BookingSummaryDTO> recentBookings;
    private List<ScheduleDTO> upcomingDepartures;
}
