package me.ensah.trainLink.seeds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.model.Seat;
import me.ensah.trainLink.model.Station;
import me.ensah.trainLink.model.Train;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.ScheduleRepository;
import me.ensah.trainLink.repository.StationRepository;
import me.ensah.trainLink.repository.TrainRepository;
import me.ensah.trainLink.repository.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // Spring will automatically inject the repositories here (Constructor
    // Injection)
    public DataInitializer(StationRepository stationRepository, TrainRepository trainRepository,
            ScheduleRepository scheduleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only add data if the database is empty to avoid duplicates on restart
        if (userRepository.count() == 0) {
            System.out.println("🌱 Seeding database with initial users...");

            // Create admin user with encoded password
            if (userRepository.findByEmail("mohssine@gmail.com").isEmpty()) {
                // Email: mohssine@gmail.com, Password: mohssine
                User admin = new User(null, "Admin User", "mohssine@gmail.com",
                        passwordEncoder.encode("mohssine"), "admin");
                userRepository.save(admin);
                System.out.println("✅ Admin user created: mohssine@gmail.com / mohssine");
            }

            // Create a regular client user for testing
            if (userRepository.findByEmail("client@trainlink.com").isEmpty()) {
                // Email: client@trainlink.com, Password: mohssine
                User client = new User(null, "Client User", "client@trainlink.com",
                        passwordEncoder.encode("mohssine"), "client");
                userRepository.save(client);
                System.out.println("✅ Client user created: client@trainlink.com / mohssine");
            }

        } else {
            // Show existing users
            System.out.println("📋 Existing users in database:");
            userRepository.findAll().forEach(user -> {
                System.out.println("  - " + user.getEmail() + " (role: " + user.getRole() + ")");
            });
        }
        if (stationRepository.count() == 0) {
            System.out.println("🌱 Seeding database with initial data...");

            // 1. Create Stations
            Station casa = new Station(null, "Casablanca Voyageurs");
            Station rabat = new Station(null, "Rabat Agdal");
            Station marrakech = new Station(null, "Marrakech");
            Station tanger = new Station(null, "Tanger Ville");

            // Save all stations to the DB
            // .saveAll() is more efficient than saving one by one its useed like:
            // stationRepository.saveAll(//heere we put list of stations )
            List<Station> stations = stationRepository.saveAll(Arrays.asList(casa, rabat, marrakech, tanger));

            // Retrieve them to ensure we have the generated IDs
            Station savedCasa = stations.get(0);
            Station savedRabat = stations.get(1);
            Station savedMarrakech = stations.get(2);
            Station savedTanger = stations.get(3);

            // 2. Create Trains
            Train alBoraq = new Train(null, "Al Boraq TGV", Seat.generateSeats(250));
            Train atlas = new Train(null, "Atlas Express", Seat.generateSeats(300));

            trainRepository.saveAll(Arrays.asList(alBoraq, atlas));

            // 3. Create Schedules
            Schedule schedule1 = new Schedule(null, alBoraq, savedCasa, savedRabat,
                    LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                    LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                    new BigDecimal("95.00"), 250);

            Schedule schedule2 = new Schedule(null, atlas, savedRabat, savedMarrakech,
                    LocalDateTime.now().plusDays(1).withHour(10).withMinute(30),
                    LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                    new BigDecimal("120.00"), 300);

            Schedule schedule3 = new Schedule(null, alBoraq, savedTanger, savedRabat,
                    LocalDateTime.now().plusDays(2).withHour(7).withMinute(0),
                    LocalDateTime.now().plusDays(2).withHour(8).withMinute(20),
                    new BigDecimal("150.00"), 200); // Partially booked

            scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2, schedule3));

            System.out.println("✅ Database has been seeded!");
        } else {
            System.out.println("Database already contains data. Skipping seeding.");
        }
    }
}