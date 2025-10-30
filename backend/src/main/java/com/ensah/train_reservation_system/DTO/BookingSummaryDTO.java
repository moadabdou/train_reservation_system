package com.ensah.train_reservation_system.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.ensah.train_reservation_system.model.BookingStatus;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingSummaryDTO {
    private Long bookingId;
    private String referenceCode;
    private Long scheduleId;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private int passengersCount;
    private BigDecimal totalPrice;
}
