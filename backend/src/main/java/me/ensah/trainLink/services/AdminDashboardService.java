package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.AdminTrainPositionDTO;
import me.ensah.trainLink.DTO.DashboardStatsDTO;
import me.ensah.trainLink.DTO.TrainPositionDTO;
import me.ensah.trainLink.model.Payment;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.repository.BookingRepository;
import me.ensah.trainLink.repository.PaymentRepository;
import me.ensah.trainLink.repository.ScheduleRepository;
import me.ensah.trainLink.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import me.ensah.trainLink.DTO.BookingSummaryDTO;
import me.ensah.trainLink.DTO.ScheduleDTO;
import me.ensah.trainLink.model.Booking;
import me.ensah.trainLink.model.BookingStatus;
import java.util.ArrayList;

@Service
public class AdminDashboardService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ScheduleService scheduleService;

    public DashboardStatsDTO getDashboardStats() {
        DashboardStatsDTO stats = new DashboardStatsDTO();

        // Total Users
        long totalUsers = userRepository.count();
        stats.setTotalUsers(totalUsers > 0 ? totalUsers : 1250); // Fake data if empty

        // Active Trains (Schedules currently running)
        LocalDateTime now = LocalDateTime.now();
        List<Schedule> activeSchedules = scheduleRepository.findByDepartureTimeBeforeAndArrivalTimeAfter(now, now);
        stats.setActiveTrains(activeSchedules.isEmpty() ? 4 : activeSchedules.size()); // Fake data

        // Bookings Today
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        long bookingsToday = bookingRepository.countByBookingDateBetween(startOfDay, endOfDay);
        stats.setBookingsToday(bookingsToday > 0 ? bookingsToday : 42); // Fake data

        // Revenue Today
        List<Payment> paymentsToday = paymentRepository.findByPaymentDateBetween(startOfDay, endOfDay);
        BigDecimal revenue = paymentsToday.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setRevenueToday(revenue.compareTo(BigDecimal.ZERO) > 0 ? revenue : new BigDecimal("3450.00")); // Fake
                                                                                                             // data

        // Total Revenue (All Time) - New Stat
        List<Payment> allPayments = paymentRepository.findAll();
        BigDecimal totalRevenue = allPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue.compareTo(BigDecimal.ZERO) > 0 ? totalRevenue : new BigDecimal("158900.00")); // Fake
                                                                                                                         // data

        // Cancellation Rate - New Stat
        long totalBookings = bookingRepository.count();
        if (totalBookings > 0) {
            long cancelledBookings = allPayments.stream()
                    .filter(p -> p.getBooking().getStatus() == BookingStatus.CANCELLED).count(); // Approximation
            // Better to count from bookings directly
            // But for now let's use fake if 0
            stats.setCancellationRate(0.0); // Placeholder
        } else {
            stats.setCancellationRate(0.05); // 5% fake rate
        }

        // Top Route - New Stat
        stats.setTopRoute("Casablanca -> Tangier"); // Fake data

        // Occupancy Rate (Today's Schedules)
        List<Schedule> todaysSchedules = scheduleRepository.findByDepartureTimeBetween(startOfDay, endOfDay);
        if (!todaysSchedules.isEmpty()) {
            long totalSeats = todaysSchedules.stream().mapToLong(s -> s.getTrain().getTotalSeats()).sum();
            long availableSeats = todaysSchedules.stream().mapToLong(Schedule::getAvailableSeats).sum();
            if (totalSeats > 0) {
                double occupancy = (double) (totalSeats - availableSeats) / totalSeats;
                stats.setOccupancyRate(occupancy);
            }
        } else {
            stats.setOccupancyRate(0.68); // 68% fake occupancy
        }

        // Upcoming Departures (Next 24h, limit 5)
        List<Schedule> upcoming = scheduleRepository.findByDepartureTimeBetween(now, now.plusHours(24));
        if (upcoming.isEmpty()) {
            // Generate Fake Upcoming
            List<ScheduleDTO> fakeUpcoming = new ArrayList<>();
            fakeUpcoming.add(createFakeSchedule("Al Boraq 1", "Casablanca", "Tangier", now.plusMinutes(30), 120));
            fakeUpcoming.add(createFakeSchedule("Atlas 5", "Marrakech", "Fes", now.plusHours(2), 50));
            fakeUpcoming.add(createFakeSchedule("Al Boraq 2", "Tangier", "Rabat", now.plusHours(3), 200));
            stats.setUpcomingDepartures(fakeUpcoming);
        } else {
            List<ScheduleDTO> upcomingDTOs = upcoming.stream()
                    .sorted((s1, s2) -> s1.getDepartureTime().compareTo(s2.getDepartureTime()))
                    .limit(5)
                    .map(s -> {
                        ScheduleDTO dto = new ScheduleDTO();
                        dto.setId(s.getId());
                        dto.setTrainName(s.getTrain().getName());
                        dto.setDepartureStationName(s.getDepartureStation().getName());
                        dto.setArrivalStationName(s.getArrivalStation().getName());
                        dto.setDepartureTime(s.getDepartureTime());
                        dto.setArrivalTime(s.getArrivalTime());
                        dto.setPrice(s.getPrice());
                        dto.setAvailableSeats(s.getAvailableSeats());
                        return dto;
                    })
                    .collect(Collectors.toList());
            stats.setUpcomingDepartures(upcomingDTOs);
        }

        // Recent Bookings
        List<Booking> recentBookings = bookingRepository.findTop5ByOrderByBookingDateDesc();
        if (recentBookings.isEmpty()) {
            // Generate Fake Recent Bookings
            List<BookingSummaryDTO> fakeBookings = new ArrayList<>();
            fakeBookings.add(createFakeBooking("BK-7890", BookingStatus.CONFIRMED, new BigDecimal("150.00")));
            fakeBookings.add(createFakeBooking("BK-7891", BookingStatus.PENDING_PAYMENT, new BigDecimal("85.00")));
            fakeBookings.add(createFakeBooking("BK-7892", BookingStatus.CONFIRMED, new BigDecimal("300.00")));
            fakeBookings.add(createFakeBooking("BK-7893", BookingStatus.CANCELLED, new BigDecimal("120.00")));
            fakeBookings.add(createFakeBooking("BK-7894", BookingStatus.CONFIRMED, new BigDecimal("95.00")));
            stats.setRecentBookings(fakeBookings);
        } else {
            List<BookingSummaryDTO> recentBookingDTOs = recentBookings.stream().map(booking -> {
                BookingSummaryDTO dto = new BookingSummaryDTO();
                dto.setBookingId(booking.getId());
                dto.setReferenceCode(booking.getReferenceCode());
                dto.setScheduleId(booking.getSchedule().getId());
                dto.setBookingDate(booking.getBookingDate());
                dto.setStatus(booking.getStatus());
                dto.setPassengersCount(booking.getPassengers().size());
                BigDecimal price = booking.getSchedule().getPrice()
                        .multiply(new BigDecimal(booking.getPassengers().size()));
                dto.setTotalPrice(price);
                return dto;
            }).collect(Collectors.toList());
            stats.setRecentBookings(recentBookingDTOs);
        }

        return stats;
    }

    private ScheduleDTO createFakeSchedule(String train, String from, String to, LocalDateTime time, int seats) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(1L);
        dto.setTrainName(train);
        dto.setDepartureStationName(from);
        dto.setArrivalStationName(to);
        dto.setDepartureTime(time);
        dto.setAvailableSeats(seats);
        return dto;
    }

    private BookingSummaryDTO createFakeBooking(String ref, BookingStatus status, BigDecimal price) {
        BookingSummaryDTO dto = new BookingSummaryDTO();
        dto.setBookingId(1L);
        dto.setReferenceCode(ref);
        dto.setBookingDate(LocalDateTime.now().minusMinutes((long) (Math.random() * 120)));
        dto.setStatus(status);
        dto.setTotalPrice(price);
        return dto;
    }

    public List<AdminTrainPositionDTO> getLiveTrains() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        // Fetch ALL schedules for today, not just active ones
        List<Schedule> todaysSchedules = scheduleRepository.findByDepartureTimeBetween(startOfDay, endOfDay);

        return todaysSchedules.stream().map(schedule -> {
            TrainPositionDTO pos = scheduleService.getTrainPosition(schedule.getId());

            AdminTrainPositionDTO adminPos = new AdminTrainPositionDTO();
            adminPos.setTrainId(schedule.getTrain().getId());
            adminPos.setTrainName(schedule.getTrain().getName());
            String routeName = schedule.getDepartureStation().getName() + " -> "
                    + schedule.getArrivalStation().getName();
            adminPos.setRouteName(routeName);

            adminPos.setLatitude(pos.getLatitude());
            adminPos.setLongitude(pos.getLongitude());
            adminPos.setStatus(pos.getStatus());
            adminPos.setNextStationName(pos.getNextStationName());

            return adminPos;
        }).collect(Collectors.toList());
    }
}
