package me.ensah.trainLink.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import me.ensah.trainLink.model.Booking;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUserId(Long userId);

	Page<Booking> findByUserId(Long userId, Pageable pageable);

	Optional<Booking> findByReferenceCode(String referenceCode);

	long countByBookingDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);

	List<Booking> findTop5ByOrderByBookingDateDesc();
}
