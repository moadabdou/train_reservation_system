package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.TrainLayout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainLayoutRepository extends JpaRepository<TrainLayout, Long> {
}
