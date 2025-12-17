package me.ensah.trainLink.repository;

import me.ensah.trainLink.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long bookingId);

    Optional<Payment> findByTransactionId(String transactionId);

    java.util.List<Payment> findByStatus(me.ensah.trainLink.model.PaymentStatus status);

    java.util.List<Payment> findByPaymentDateBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
