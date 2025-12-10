package me.ensah.trainLink.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.ensah.trainLink.DTO.BookingResponse;
import me.ensah.trainLink.DTO.BookingSummaryDTO;
import me.ensah.trainLink.DTO.CreateBookingRequest;
import me.ensah.trainLink.services.BookingService;

import org.springframework.data.domain.Page;

import me.ensah.trainLink.DTO.ReceiptDTO;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/{referenceCode}/receipt")
    public ResponseEntity<ReceiptDTO> getReceipt(@PathVariable String referenceCode) {
        return ResponseEntity.ok(bookingService.generateReceipt(referenceCode));
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
