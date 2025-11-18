package me.ensah.trainLink.seeds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.model.Station;
import me.ensah.trainLink.model.Train;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.ScheduleRepository;
import me.ensah.trainLink.repository.StationRepository;
import me.ensah.trainLink.repository.TrainRepository;
import me.ensah.trainLink.repository.UserRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Comprehensive database seeder for the TrainLink application
 * Seeds stations, trains, schedules, and users with realistic data
 */
@Component
public class DataInitializer_copy implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    private final Random random = new Random();

    public DataInitializer_copy(
            StationRepository stationRepository, 
            TrainRepository trainRepository, 
            ScheduleRepository scheduleRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only seed if database is empty
        if (stationRepository.count() == 0) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🌱 Starting database seeding process...");
            System.out.println("=".repeat(60) + "\n");

            // Seed in order: Users -> Stations -> Trains -> Schedules
            seedUsers();
            List<Station> stations = seedStations();
            List<Train> trains = seedTrains();
            seedSchedules(stations, trains);

            System.out.println("\n" + "=".repeat(60));
            System.out.println("✅ Database seeding completed successfully!");
            System.out.println("=".repeat(60) + "\n");
            
            printSeedingSummary();
        } else {
            System.out.println("ℹ️  Database already contains data. Skipping seeding.");
        }
    }

    /**
     * Seeds user accounts (admin and test users)
     */
    private void seedUsers() {
        System.out.println("👥 Seeding users...");
        
        List<User> users = Arrays.asList(
            // Admin user
            new User(null, "Admin User", "admin@trainlink.ma", 
                    passwordEncoder.encode("admin123"), "admin"),
            
            // Test client users
            new User(null, "Mohamed Alami", "mohamed.alami@gmail.com", 
                    passwordEncoder.encode("password123"), "client"),
            
            new User(null, "Fatima Zahra", "fatima.zahra@gmail.com", 
                    passwordEncoder.encode("password123"), "client"),
            
            new User(null, "Ahmed Bennis", "ahmed.bennis@gmail.com", 
                    passwordEncoder.encode("password123"), "client"),
            
            new User(null, "Salma Idrissi", "salma.idrissi@gmail.com", 
                    passwordEncoder.encode("password123"), "client"),
            
            new User(null, "Youssef Tazi", "youssef.tazi@gmail.com", 
                    passwordEncoder.encode("password123"), "client")
        );
        
        userRepository.saveAll(users);
        System.out.println("   ✓ Created " + users.size() + " users (1 admin, " + (users.size() - 1) + " clients)");
    }

    /**
     * Seeds Moroccan train stations
     */
    private List<Station> seedStations() {
        System.out.println("\n🚉 Seeding stations...");
        
        List<Station> stations = Arrays.asList(
            // Major cities
            new Station(null, "Casablanca Voyageurs"),
            new Station(null, "Casablanca Port"),
            new Station(null, "Rabat Agdal"),
            new Station(null, "Rabat Ville"),
            new Station(null, "Marrakech"),
            new Station(null, "Tanger Ville"),
            new Station(null, "Fès Ville"),
            new Station(null, "Meknès"),
            new Station(null, "Oujda"),
            new Station(null, "Kenitra"),
            new Station(null, "El Jadida"),
            new Station(null, "Safi"),
            new Station(null, "Mohammedia"),
            new Station(null, "Settat"),
            new Station(null, "Khouribga"),
            new Station(null, "Benguerir"),
            new Station(null, "Sidi Kacem")
        );
        
        List<Station> savedStations = stationRepository.saveAll(stations);
        System.out.println("   ✓ Created " + savedStations.size() + " stations across Morocco");
        
        return savedStations;
    }

    /**
     * Seeds different types of trains with varying capacities
     */
    private List<Train> seedTrains() {
        System.out.println("\n🚄 Seeding trains...");
        
        List<Train> trains = Arrays.asList(
            // High-speed trains (TGV)
            new Train(null, "Al Boraq TGV-001", 250),
            new Train(null, "Al Boraq TGV-002", 250),
            new Train(null, "Al Boraq TGV-003", 250),
            
            // Express trains
            new Train(null, "Atlas Express", 350),
            new Train(null, "Chems Express", 350),
            new Train(null, "Rif Express", 300),
            new Train(null, "Sahara Express", 300),
            
            // Regional trains
            new Train(null, "TNR Régional 101", 400),
            new Train(null, "TNR Régional 102", 400),
            new Train(null, "TNR Régional 103", 450),
            
            // Inter-city trains
            new Train(null, "Intercity Rabat-Casa", 320),
            new Train(null, "Intercity Fès-Meknès", 280),
            new Train(null, "Intercity Tanger-Nord", 300)
        );
        
        List<Train> savedTrains = trainRepository.saveAll(trains);
        System.out.println("   ✓ Created " + savedTrains.size() + " trains");
        System.out.println("      - TGV: 3 trains (250 seats each)");
        System.out.println("      - Express: 4 trains (300-350 seats)");
        System.out.println("      - Regional: 3 trains (400-450 seats)");
        System.out.println("      - Inter-city: 3 trains (280-320 seats)");
        
        return savedTrains;
    }

    /**
     * Seeds realistic train schedules for the next 7 days
     */
    private void seedSchedules(List<Station> stations, List<Train> trains) {
        System.out.println("\n📅 Seeding schedules...");
        
        List<Schedule> schedules = new ArrayList<>();
        
        // Popular routes with their typical journey times (in hours)
        List<RouteInfo> routes = Arrays.asList(
            new RouteInfo("Casablanca Voyageurs", "Rabat Agdal", 1.0, new BigDecimal("50.00")),
            new RouteInfo("Casablanca Voyageurs", "Marrakech", 3.5, new BigDecimal("120.00")),
            new RouteInfo("Casablanca Voyageurs", "Tanger Ville", 5.0, new BigDecimal("180.00")),
            new RouteInfo("Rabat Agdal", "Tanger Ville", 4.0, new BigDecimal("150.00")),
            new RouteInfo("Rabat Agdal", "Fès Ville", 3.5, new BigDecimal("130.00")),
            new RouteInfo("Casablanca Voyageurs", "Fès Ville", 4.0, new BigDecimal("140.00")),
            new RouteInfo("Marrakech", "Casablanca Voyageurs", 3.5, new BigDecimal("120.00")),
            new RouteInfo("Tanger Ville", "Casablanca Voyageurs", 5.0, new BigDecimal("180.00")),
            new RouteInfo("Fès Ville", "Meknès", 1.0, new BigDecimal("40.00")),
            new RouteInfo("Casablanca Voyageurs", "Kenitra", 1.5, new BigDecimal("60.00")),
            new RouteInfo("Rabat Ville", "Casablanca Port", 1.0, new BigDecimal("45.00")),
            new RouteInfo("Kenitra", "Tanger Ville", 3.0, new BigDecimal("110.00")),
            new RouteInfo("Casablanca Voyageurs", "El Jadida", 1.5, new BigDecimal("55.00")),
            new RouteInfo("Rabat Agdal", "Meknès", 2.0, new BigDecimal("80.00")),
            new RouteInfo("Settat", "Marrakech", 2.0, new BigDecimal("90.00"))
        );

        // Generate schedules for the next 7 days
        LocalDateTime now = LocalDateTime.now();
        int scheduleCount = 0;
        
        for (int day = 0; day < 7; day++) {
            LocalDateTime targetDate = now.plusDays(day);
            
            for (RouteInfo route : routes) {
                Station departure = findStationByName(stations, route.departure);
                Station arrival = findStationByName(stations, route.arrival);
                
                if (departure != null && arrival != null) {
                    // Create 3-5 schedules per route per day at different times
                    int schedulesPerDay = 3 + random.nextInt(3); // 3 to 5 schedules
                    
                    for (int i = 0; i < schedulesPerDay; i++) {
                        Train train = trains.get(random.nextInt(trains.size()));
                        
                        // Different departure times throughout the day
                        LocalTime departureTime = getDepartureTimeSlot(i, schedulesPerDay);
                        LocalDateTime departure_dt = targetDate.with(departureTime);
                        LocalDateTime arrival_dt = departure_dt.plusMinutes((long)(route.journeyHours * 60));
                        
                        // Randomize available seats (70% to 100% of capacity)
                        int availableSeats = train.getTotalSeats() - random.nextInt((int)(train.getTotalSeats() * 0.3));
                        
                        // Add some price variation (±10%)
                        BigDecimal priceVariation = route.basePrice.multiply(
                            new BigDecimal(0.9 + (random.nextDouble() * 0.2))
                        ).setScale(2, RoundingMode.HALF_UP);
                        
                        Schedule schedule = new Schedule(
                            null,
                            train,
                            departure,
                            arrival,
                            departure_dt,
                            arrival_dt,
                            priceVariation,
                            availableSeats
                        );
                        
                        schedules.add(schedule);
                        scheduleCount++;
                    }
                }
            }
        }
        
        scheduleRepository.saveAll(schedules);
        System.out.println("   ✓ Created " + scheduleCount + " schedules");
        System.out.println("      - " + routes.size() + " unique routes");
        System.out.println("      - 7 days of scheduling");
        System.out.println("      - 3-5 trips per route per day");
    }

    /**
     * Gets departure time slot based on schedule index
     */
    private LocalTime getDepartureTimeSlot(int index, int totalSlots) {
        // Distribute times evenly throughout the day (6 AM to 10 PM)
        int startHour = 6;
        int endHour = 22;
        int hourRange = endHour - startHour;
        
        int slotDuration = hourRange / totalSlots;
        int hour = startHour + (index * slotDuration);
        int minute = random.nextInt(4) * 15; // 0, 15, 30, or 45 minutes
        
        return LocalTime.of(hour, minute);
    }

    /**
     * Helper method to find station by name
     */
    private Station findStationByName(List<Station> stations, String name) {
        return stations.stream()
            .filter(s -> s.getName().equals(name))
            .findFirst()
            .orElse(null);
    }

    /**
     * Prints a summary of seeded data
     */
    private void printSeedingSummary() {
        System.out.println("📊 Seeding Summary:");
        System.out.println("   • Users: " + userRepository.count());
        System.out.println("   • Stations: " + stationRepository.count());
        System.out.println("   • Trains: " + trainRepository.count());
        System.out.println("   • Schedules: " + scheduleRepository.count());
        System.out.println("\n💡 Test Credentials:");
        System.out.println("   Admin:  admin@trainlink.ma / admin123");
        System.out.println("   Client: mohamed.alami@gmail.com / password123");
        System.out.println("   (or any other seeded user with password123)\n");
    }

    /**
     * Helper class to store route information
     */
    private static class RouteInfo {
        String departure;
        String arrival;
        double journeyHours;
        BigDecimal basePrice;

        RouteInfo(String departure, String arrival, double journeyHours, BigDecimal basePrice) {
            this.departure = departure;
            this.arrival = arrival;
            this.journeyHours = journeyHours;
            this.basePrice = basePrice;
        }
    }
}