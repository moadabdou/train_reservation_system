package me.ensah.trainLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.ensah.trainLink.model.Train;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {
    Train findByName(String name);
}
