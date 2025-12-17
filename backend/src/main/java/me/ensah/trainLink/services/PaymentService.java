package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.PaymentRequest;
import me.ensah.trainLink.DTO.PaymentResponse;
import me.ensah.trainLink.model.*;
import me.ensah.trainLink.repository.BookingRepository;
import me.ensah.trainLink.repository.PaymentRepository;
import me.ensah.trainLink.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final RefundRepository refundRepository;
    private final BookingRepository bookingRepository;
    private final LoyaltyService loyaltyService;

    public PaymentService(PaymentRepository paymentRepository, RefundRepository refundRepository,
            BookingRepository bookingRepository, LoyaltyService loyaltyService) {
        this.paymentRepository = paymentRepository;
        this.refundRepository = refundRepository;
        this.bookingRepository = bookingRepository;
        this.loyaltyService = loyaltyService;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest request) {
        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            throw new IllegalStateException("Booking is already confirmed");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay for a cancelled booking");
        }

        // Mock payment validation
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid amount");
        }

        // Create Payment record
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionId(UUID.randomUUID().toString());

        Payment savedPayment = paymentRepository.save(payment);

        // Update booking status
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        // Award loyalty points
        loyaltyService.awardPoints(booking.getUser(), request.getAmount().doubleValue());

        return new PaymentResponse(
                savedPayment.getId(),
                booking.getId(),
                savedPayment.getAmount(),
                savedPayment.getStatus(),
                savedPayment.getTransactionId(),
                savedPayment.getPaymentDate());
    }

    public Payment getPaymentByBooking(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElse(null);
    }

    public PaymentResponse getReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate());
    }

    @Transactional
    public void refundPayment(Booking booking) {
        Payment payment = paymentRepository.findByBookingId(booking.getId())
                .orElseThrow(() -> new IllegalStateException("No payment found for this booking"));

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return; // Already refunded
        }

        // Calculate refund amount (Mock logic: 100% refund)
        BigDecimal refundAmount = payment.getAmount();

        Refund refund = new Refund();
        refund.setPayment(payment);
        refund.setAmount(refundAmount);
        refund.setRefundDate(LocalDateTime.now());
        refund.setReason("Booking Cancelled");

        refundRepository.save(refund);

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }

    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getBooking().getId(),
                payment.getAmount(),
                payment.getStatus(),
                payment.getTransactionId(),
                payment.getPaymentDate());
    }
}
