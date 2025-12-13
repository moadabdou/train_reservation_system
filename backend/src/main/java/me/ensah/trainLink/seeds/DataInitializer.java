package me.ensah.trainLink.seeds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import me.ensah.trainLink.model.RouteStop;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.model.Seat;
import me.ensah.trainLink.model.Station;
import me.ensah.trainLink.model.Train;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.model.Reward;
import me.ensah.trainLink.model.Provider;
import me.ensah.trainLink.repository.RouteStopRepository;
import me.ensah.trainLink.repository.ScheduleRepository;
import me.ensah.trainLink.repository.StationRepository;
import me.ensah.trainLink.repository.TrainRepository;
import me.ensah.trainLink.repository.UserRepository;
import me.ensah.trainLink.repository.RewardRepository;
import me.ensah.trainLink.repository.ProviderRepository;

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
    private final RouteStopRepository routeStopRepository;
    private final RewardRepository rewardRepository;
    private final ProviderRepository providerRepository;

    // Spring will automatically inject the repositories here (Constructor
    // Injection)
    public DataInitializer(StationRepository stationRepository, TrainRepository trainRepository,
            ScheduleRepository scheduleRepository, UserRepository userRepository,
            PasswordEncoder passwordEncoder, RouteStopRepository routeStopRepository,
            RewardRepository rewardRepository, ProviderRepository providerRepository) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.routeStopRepository = routeStopRepository;
        this.rewardRepository = rewardRepository;
        this.providerRepository = providerRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        try {
            // Update coordinates for existing stations if they are missing
            // updateStationCoordinates(); // Commented out to prevent connection issues for
            // now

            // Ensure Kenitra exists (it might be missing from initial seed)
            Station kenitra = stationRepository.findByName("Kenitra");
            if (kenitra == null) {
                kenitra = new Station(null, "Kenitra", 34.2610, -6.5802, "Major transit hub", null, null);
                kenitra = stationRepository.save(kenitra);
                System.out.println("✅ Created missing station: Kenitra");
            }

            // Ensure Admin user exists
            if (userRepository.findByEmail("mohssine@gmail.com").isEmpty()) {
                // Email: mohssine@gmail.com, Password: mohssine
                User admin = new User(null, "Admin User", "mohssine@gmail.com",
                        passwordEncoder.encode("mohssine"), "admin");
                userRepository.save(admin);
                System.out.println("✅ Admin user created: mohssine@gmail.com / mohssine");
            }

            // Ensure Client user exists
            if (userRepository.findByEmail("client@trainlink.com").isEmpty()) {
                // Email: client@trainlink.com, Password: mohssine
                User client = new User(null, "Client User", "client@trainlink.com",
                        passwordEncoder.encode("mohssine"), "client");
                userRepository.save(client);
                System.out.println("✅ Client user created: client@trainlink.com / mohssine");
            }

            // 0. Create Provider (ONCF)
            Provider oncf = providerRepository.findByName("ONCF");
            if (oncf == null) {
                oncf = new Provider(null, "ONCF",
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/ONCF_Logo.svg/1200px-ONCF_Logo.svg.png",
                        "contact@oncf.ma", null);
                oncf = providerRepository.save(oncf);
                System.out.println("✅ Created Provider: ONCF");
            } else {
                // Update logo if it's missing or different (optional, but good for fixing
                // broken data)
                oncf.setLogoUrl(
                        "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e6/ONCF_Logo.svg/1200px-ONCF_Logo.svg.png");
                providerRepository.save(oncf);
                System.out.println("🔄 Updated Provider: ONCF logo");
            }
            // 1. Create Stations (if not exist)
            Station casa = stationRepository.findByName("Casablanca Voyageurs");
            if (casa == null)
                casa = stationRepository.save(
                        new Station(null, "Casablanca Voyageurs", 33.5898, -7.5898, "Main station in Casablanca", null,
                                null));

            Station rabat = stationRepository.findByName("Rabat Agdal");
            if (rabat == null)
                rabat = stationRepository
                        .save(new Station(null, "Rabat Agdal", 34.0033, -6.8558, "Modern station in Rabat", null,
                                null));

            Station marrakech = stationRepository.findByName("Marrakech");
            if (marrakech == null)
                marrakech = stationRepository
                        .save(new Station(null, "Marrakech", 31.6346, -8.0156, "Beautiful station in Marrakech", null,
                                null));

            Station tanger = stationRepository.findByName("Tanger Ville");
            if (tanger == null)
                tanger = stationRepository
                        .save(new Station(null, "Tanger Ville", 35.7733, -5.8025, "Gateway to Europe", null, null));

            // 2. Create Trains
            Train alBoraq = trainRepository.findByName("Al Boraq TGV");
            if (alBoraq == null) {
                alBoraq = trainRepository.save(new Train(null, "Al Boraq TGV", oncf, Seat.generateSeats(250)));
                System.out.println("✅ Created Train: Al Boraq TGV");
            } else if (alBoraq.getProvider() == null) {
                alBoraq.setProvider(oncf);
                trainRepository.save(alBoraq);
                System.out.println("🔄 Updated Train: Al Boraq TGV with Provider");
            }

            Train atlas = trainRepository.findByName("Atlas Express");
            if (atlas == null) {
                atlas = trainRepository.save(new Train(null, "Atlas Express", oncf, Seat.generateSeats(300)));
                System.out.println("✅ Created Train: Atlas Express");
            } else if (atlas.getProvider() == null) {
                atlas.setProvider(oncf);
                trainRepository.save(atlas);
                System.out.println("🔄 Updated Train: Atlas Express with Provider");
            }

            // 3. Create Schedules (Only if we don't have them yet)
            if (scheduleRepository.count() < 3) {
                Schedule schedule1 = new Schedule(null, alBoraq, casa, rabat,
                        LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                        LocalDateTime.now().plusDays(1).withHour(9).withMinute(0),
                        new BigDecimal("95.00"), 250);

                Schedule schedule2 = new Schedule(null, atlas, rabat, marrakech,
                        LocalDateTime.now().plusDays(1).withHour(10).withMinute(30),
                        LocalDateTime.now().plusDays(1).withHour(14).withMinute(0),
                        new BigDecimal("120.00"), 300);

                Schedule schedule3 = new Schedule(null, alBoraq, tanger, rabat,
                        LocalDateTime.now().plusDays(2).withHour(7).withMinute(0),
                        LocalDateTime.now().plusDays(2).withHour(8).withMinute(20),
                        new BigDecimal("150.00"), 200); // Partially booked

                scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2, schedule3));
                System.out.println("✅ Created initial schedules");
            }

            // 4. Create Demo Schedule with Intermediate Stops: Casablanca -> Tanger
            // Check if we already have a schedule from Casa to Tanger
            List<Schedule> casaToTanger = scheduleRepository
                    .findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
                            casa.getId(), tanger.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(30));

            if (casaToTanger.isEmpty()) {
                Schedule demoSchedule = new Schedule(null, alBoraq, casa, tanger,
                        LocalDateTime.now().plusDays(1).withHour(8).withMinute(0),
                        LocalDateTime.now().plusDays(1).withHour(10).withMinute(10),
                        new BigDecimal("200.00"), 250);

                demoSchedule = scheduleRepository.save(demoSchedule);

                // Add Intermediate Stops for Demo Schedule
                RouteStop stopRabat = new RouteStop(null, demoSchedule, rabat,
                        demoSchedule.getDepartureTime().plusMinutes(50), // Arr 08:50
                        demoSchedule.getDepartureTime().plusMinutes(60), // Dep 09:00
                        1);

                RouteStop stopKenitra = new RouteStop(null, demoSchedule, kenitra,
                        demoSchedule.getDepartureTime().plusMinutes(90), // Arr 09:30
                        demoSchedule.getDepartureTime().plusMinutes(100), // Dep 09:40
                        2);

                routeStopRepository.saveAll(Arrays.asList(stopRabat, stopKenitra));
                System.out
                        .println("✅ Created Demo Schedule (ID: " + demoSchedule.getId() + ") with intermediate stops!");
            } else {
                System.out.println("ℹ️ Demo Schedule already exists. Checking for stops...");
                Schedule existingDemo = casaToTanger.get(0);
                List<RouteStop> existingStops = routeStopRepository
                        .findByScheduleIdOrderByStopOrderAsc(existingDemo.getId());

                if (existingStops.isEmpty()) {
                    System.out.println("⚠️ Existing Demo Schedule has no stops. Adding them now...");

                    // Ensure the time is correct for the demo (08:00 -> 10:10)
                    // We update the arrival time to match our demo scenario
                    existingDemo.setArrivalTime(existingDemo.getDepartureTime().plusHours(2).plusMinutes(10));
                    scheduleRepository.save(existingDemo);

                    RouteStop stopRabat = new RouteStop(null, existingDemo, rabat,
                            existingDemo.getDepartureTime().plusMinutes(50), // Arr 08:50
                            existingDemo.getDepartureTime().plusMinutes(60), // Dep 09:00
                            1);

                    RouteStop stopKenitra = new RouteStop(null, existingDemo, kenitra,
                            existingDemo.getDepartureTime().plusMinutes(90), // Arr 09:30
                            existingDemo.getDepartureTime().plusMinutes(100), // Dep 09:40
                            2);

                    routeStopRepository.saveAll(Arrays.asList(stopRabat, stopKenitra));
                    System.out.println("✅ Added missing intermediate stops to existing Demo Schedule (ID: "
                            + existingDemo.getId() + ")");
                } else {
                    System.out.println("✅ Existing Demo Schedule already has " + existingStops.size() + " stops.");
                }
            }

            System.out.println("✅ Database seeding completed!");

            // 5. Create Rewards
            if (rewardRepository.count() == 0) {
                Reward reward1 = new Reward(null, 500, "10% Discount Voucher", 10.0, "DISCOUNT");
                Reward reward2 = new Reward(null, 1000, "Free Upgrade to First Class", null, "UPGRADE");
                Reward reward3 = new Reward(null, 2000, "Free Ticket (One Way)", null, "FREE_TICKET");

                rewardRepository.saveAll(Arrays.asList(reward1, reward2, reward3));
                System.out.println("✅ Created initial rewards");
            }
        } catch (Exception e) {
            System.err.println("Error during data initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateStationCoordinates() {
        List<Station> stations = stationRepository.findAll();
        boolean updated = false;
        for (Station station : stations) {
            if (station.getLatitude() == null || station.getLongitude() == null) {
                switch (station.getName()) {
                    case "Casablanca Voyageurs":
                        station.setLatitude(33.5898);
                        station.setLongitude(-7.5898);
                        break;
                    case "Rabat Agdal":
                        station.setLatitude(34.0033);
                        station.setLongitude(-6.8558);
                        break;
                    case "Marrakech":
                        station.setLatitude(31.6346);
                        station.setLongitude(-8.0156);
                        break;
                    case "Tanger Ville":
                        station.setLatitude(35.7733);
                        station.setLongitude(-5.8025);
                        break;
                    // Add more if needed from the other initializer
                    case "Casablanca Port":
                        station.setLatitude(33.6000);
                        station.setLongitude(-7.6167);
                        break;
                    case "Rabat Ville":
                        station.setLatitude(34.0167);
                        station.setLongitude(-6.8333);
                        break;
                    case "Fès Ville":
                        station.setLatitude(34.0333);
                        station.setLongitude(-5.0000);
                        break;
                    case "Meknès":
                        station.setLatitude(33.8935);
                        station.setLongitude(-5.5473);
                        break;
                    case "Oujda":
                        station.setLatitude(34.6867);
                        station.setLongitude(-1.9114);
                        break;
                    case "Kenitra":
                        station.setLatitude(34.2610);
                        station.setLongitude(-6.5802);
                        break;
                    case "El Jadida":
                        station.setLatitude(33.2316);
                        station.setLongitude(-8.5007);
                        break;
                    case "Safi":
                        station.setLatitude(32.2994);
                        station.setLongitude(-9.2372);
                        break;
                    case "Mohammedia":
                        station.setLatitude(33.6833);
                        station.setLongitude(-7.3833);
                        break;
                    case "Settat":
                        station.setLatitude(33.0000);
                        station.setLongitude(-7.6167);
                        break;
                    case "Khouribga":
                        station.setLatitude(32.8833);
                        station.setLongitude(-6.9167);
                        break;
                    case "Benguerir":
                        station.setLatitude(32.2333);
                        station.setLongitude(-7.9500);
                        break;
                    case "Sidi Kacem":
                        station.setLatitude(34.2167);
                        station.setLongitude(-5.7000);
                        break;
                }
                if (station.getLatitude() != null) {
                    stationRepository.save(station);
                    updated = true;
                }
            }
        }
        if (updated) {
            System.out.println("🔄 Updated missing station coordinates.");
        }
    }
}