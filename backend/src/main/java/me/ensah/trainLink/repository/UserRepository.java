package me.ensah.trainLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.ensah.trainLink.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}