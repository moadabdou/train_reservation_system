package me.ensah.trainLink.DTO;

import lombok.Data;
import me.ensah.trainLink.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentResponse {
    private Long id;
    private Long bookingId;
    private BigDecimal amount;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime paymentDate;
}
