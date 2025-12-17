package me.ensah.trainLink.services;

import me.ensah.trainLink.DTO.FinancialStatsDTO;
import me.ensah.trainLink.model.Payment;
import me.ensah.trainLink.model.PaymentStatus;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.repository.PaymentRepository;
import me.ensah.trainLink.repository.RefundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private RefundRepository refundRepository;

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment validatePayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setStatus(PaymentStatus.COMPLETED);
        return paymentRepository.save(payment);
    }

    public FinancialStatsDTO getFinancialStats() {
        FinancialStatsDTO stats = new FinancialStatsDTO();

        List<Payment> completedPayments = paymentRepository.findByStatus(PaymentStatus.COMPLETED);

        // Total Revenue
        BigDecimal totalRevenue = completedPayments.stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRevenue(totalRevenue);

        // Total Refunds
        BigDecimal totalRefunds = refundRepository.findAll().stream()
                .map(me.ensah.trainLink.model.Refund::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.setTotalRefunds(totalRefunds);

        // Revenue by Route
        Map<String, BigDecimal> revenueByRoute = new HashMap<>();
        for (Payment payment : completedPayments) {
            if (payment.getBooking() != null && payment.getBooking().getSchedule() != null) {
                Schedule schedule = payment.getBooking().getSchedule();
                String routeName = schedule.getDepartureStation().getName() + " -> "
                        + schedule.getArrivalStation().getName();

                revenueByRoute.merge(routeName, payment.getAmount(), BigDecimal::add);
            }
        }
        stats.setRevenueByRoute(revenueByRoute);

        return stats;
    }
}
