package me.ensah.trainLink.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    private Long bookingId;
    private String paymentMethod;
    private BigDecimal amount;
    // Mock card details
    private String cardNumber;
    private String expiryDate;
    private String cvv;
}
