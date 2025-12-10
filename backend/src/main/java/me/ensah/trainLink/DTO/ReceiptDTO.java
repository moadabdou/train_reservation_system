package me.ensah.trainLink.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiptDTO {
    private String bookingReference;
    private LocalDateTime bookingDate;
    private String trainName;
    private String departureStation;
    private String arrivalStation;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private List<String> passengerNames;
    private BigDecimal totalAmount;
    private String paymentStatus;
    private String transactionId;
}
