package com.ensah.train_reservation_system.repository;

import com.ensah.train_reservation_system.model.Schedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    
    List<Schedule> findByDepartureStationIdAndArrivalStationIdAndDepartureTimeBetween(
        Long departureStationId, 
        Long arrivalStationId, 
        LocalDateTime startTime, 
        LocalDateTime endTime
    );

    // Atomically decrement available seats if enough seats are available
    @Modifying
    @Transactional
    @Query("UPDATE Schedule s SET s.availableSeats = s.availableSeats - :count WHERE s.id = :scheduleId AND s.availableSeats >= :count")
    int decrementAvailableSeats(@Param("scheduleId") Long scheduleId, @Param("count") int count);

    // Increment available seats (used when cancelling a booking)
    @Modifying
    @Transactional
    @Query("UPDATE Schedule s SET s.availableSeats = s.availableSeats + :count WHERE s.id = :scheduleId")
    int incrementAvailableSeats(@Param("scheduleId") Long scheduleId, @Param("count") int count);

}
   
