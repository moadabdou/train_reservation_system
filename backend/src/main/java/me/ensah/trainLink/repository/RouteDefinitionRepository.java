package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.RouteDefinition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteDefinitionRepository extends JpaRepository<RouteDefinition, Long> {
    List<RouteDefinition> findByRouteIdOrderByStopOrderAsc(Long routeId);
}
