package me.ensah.trainLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.ensah.trainLink.model.Station;



@Repository
public interface StationRepository extends JpaRepository<Station, Long> {}
