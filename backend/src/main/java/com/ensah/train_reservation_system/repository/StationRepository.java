package com.ensah.train_reservation_system.repository;

import com.ensah.train_reservation_system.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface StationRepository extends JpaRepository<Station, Long> {}
