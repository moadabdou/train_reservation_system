package com.ensah.train_reservation_system.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ensah.train_reservation_system.model.BookingStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String referenceCode;
    private Long scheduleId;
    private LocalDateTime bookingDate;
    private BookingStatus status;
    private List<PassengerDTO> passengers;
    private BigDecimal totalPrice;
}
