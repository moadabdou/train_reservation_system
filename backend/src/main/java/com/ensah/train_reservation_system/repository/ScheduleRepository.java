package com.ensah.train_reservation_system.repository;

import com.ensah.train_reservation_system.model.Schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
        Long departureStationId, 
        Long arrivalStationId, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

}
   
