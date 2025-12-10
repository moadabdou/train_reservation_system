package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.RouteStop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteStopRepository extends JpaRepository<RouteStop, Long> {
    List<RouteStop> findByScheduleIdOrderByStopOrderAsc(Long scheduleId);
}
