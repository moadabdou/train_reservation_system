package com.ensah.train_reservation_system.controller;

import com.ensah.train_reservation_system.DTO.BookingResponse;
import com.ensah.train_reservation_system.DTO.BookingSummaryDTO;
import com.ensah.train_reservation_system.DTO.CreateBookingRequest;
import com.ensah.train_reservation_system.services.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(bookingService.createBooking(request));
    }

    @GetMapping
    public ResponseEntity<Page<BookingSummaryDTO>> listMyBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(bookingService.listMyBookings(page, size));
    }

    @GetMapping("/{referenceCode}")
    public ResponseEntity<BookingResponse> getMyBooking(@PathVariable String referenceCode) {
        return ResponseEntity.ok(bookingService.getMyBookingByRef(referenceCode));
    }

    @DeleteMapping("/{referenceCode}")
    public ResponseEntity<Void> cancelMyBooking(@PathVariable String referenceCode) {
        bookingService.cancelMyBooking(referenceCode);
        return ResponseEntity.noContent().build();
    }
}
