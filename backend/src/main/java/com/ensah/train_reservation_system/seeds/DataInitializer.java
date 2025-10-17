package com.ensah.train_reservation_system.seeds;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

// Import your entity and repository classes
import com.ensah.train_reservation_system.model.Station;
import com.ensah.train_reservation_system.model.Train;
import com.ensah.train_reservation_system.model.Schedule;
import com.ensah.train_reservation_system.repository.StationRepository;
import com.ensah.train_reservation_system.repository.TrainRepository;
import com.ensah.train_reservation_system.repository.ScheduleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final ScheduleRepository scheduleRepository;

    // Spring will automatically inject the repositories here (Constructor Injection)
    public DataInitializer(StationRepository stationRepository, TrainRepository trainRepository, ScheduleRepository scheduleRepository) {
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.scheduleRepository = scheduleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Only add data if the database is empty to avoid duplicates on restart
        if (stationRepository.count() == 0) {
            System.out.println("🌱 Seeding database with initial data...");

            // 1. Create Stations
            Station casa = new Station(null, "Casablanca Voyageurs");
            Station rabat = new Station(null, "Rabat Agdal");
            Station marrakech = new Station(null, "Marrakech");
            Station tanger = new Station(null, "Tanger Ville");
            
            // Save all stations to the DB
            // .saveAll() is more efficient than saving one by one
            List<Station> stations = stationRepository.saveAll(Arrays.asList(casa, rabat, marrakech, tanger));
            
            // Retrieve them to ensure we have the generated IDs
            Station savedCasa = stations.get(0);
            Station savedRabat = stations.get(1);
            Station savedMarrakech = stations.get(2);
            Station savedTanger = stations.get(3);

            // 2. Create Trains
            Train alBoraq = new Train(null, "Al Boraq TGV", 250);
            Train atlas = new Train(null, "Atlas Express", 300);
            
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