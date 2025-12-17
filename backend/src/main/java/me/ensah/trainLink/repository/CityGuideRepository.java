package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.CityGuide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityGuideRepository extends JpaRepository<CityGuide, Long> {
    Optional<CityGuide> findByCityName(String cityName);
}
