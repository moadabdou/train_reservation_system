package com.ensah.train_reservation_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ensah.train_reservation_system.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}