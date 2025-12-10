package me.ensah.trainLink.services;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HexFormat;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import me.ensah.trainLink.DTO.BookingResponse;
import me.ensah.trainLink.DTO.BookingSummaryDTO;
import me.ensah.trainLink.DTO.CreateBookingRequest;
import me.ensah.trainLink.DTO.PassengerDTO;
import me.ensah.trainLink.model.Booking;
import me.ensah.trainLink.model.BookingStatus;
import me.ensah.trainLink.model.Passenger;
import me.ensah.trainLink.model.Schedule;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.BookingRepository;
import me.ensah.trainLink.repository.PassengerRepository;
import me.ensah.trainLink.repository.ScheduleRepository;
import me.ensah.trainLink.repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import me.ensah.trainLink.DTO.ReceiptDTO;
import me.ensah.trainLink.model.Payment;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PassengerRepository passengerRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final SecureRandom secureRandom = new SecureRandom();

    public BookingService(BookingRepository bookingRepository, PassengerRepository passengerRepository,
            ScheduleRepository scheduleRepository, UserRepository userRepository,
            PaymentService paymentService) {
        this.bookingRepository = bookingRepository;
        this.passengerRepository = passengerRepository;
        this.scheduleRepository = scheduleRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    public ReceiptDTO generateReceipt(String referenceCode) {
        Booking booking = bookingRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        if (!booking.getUser().getEmail().equals(currentUsername)) {
            throw new SecurityException("Unauthorized access to booking");
        }

        Payment payment = paymentService.getPaymentByBooking(booking.getId());

        ReceiptDTO receipt = new ReceiptDTO();
        receipt.setBookingReference(booking.getReferenceCode());
        receipt.setBookingDate(booking.getBookingDate());
        receipt.setTrainName(booking.getSchedule().getTrain().getName());
        receipt.setDepartureStation(booking.getSchedule().getDepartureStation().getName());
        receipt.setArrivalStation(booking.getSchedule().getArrivalStation().getName());
        receipt.setDepartureTime(booking.getSchedule().getDepartureTime());
        receipt.setArrivalTime(booking.getSchedule().getArrivalTime());
        receipt.setPassengerNames(
                booking.getPassengers().stream().map(Passenger::getName).collect(Collectors.toList()));

        if (payment != null) {
            receipt.setTotalAmount(payment.getAmount());
            receipt.setPaymentStatus(payment.getStatus().name());
            receipt.setTransactionId(payment.getTransactionId());
        } else {
            // Fallback if payment not found (e.g. pending or old data)
            receipt.setTotalAmount(
                    booking.getSchedule().getPrice().multiply(BigDecimal.valueOf(booking.getPassengers().size())));
            receipt.setPaymentStatus("PENDING");
        }

        return receipt;
    }

    @Transactional
    public BookingResponse createBooking(CreateBookingRequest request) {
        if (request.getPassengers() == null || request.getPassengers().isEmpty()) {
            throw new IllegalArgumentException("Passengers list cannot be empty");
        }

        // Resolve current user by email from SecurityContext
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user;

        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            // Fallback for testing: use the default client user
            user = userRepository.findByEmail("client@trainlink.com")
                    .orElseThrow(() -> new IllegalStateException(
                            "Default client user not found. Please run DataInitializer."));
        } else {
            String email = auth.getName();
            user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new IllegalStateException("User not found"));
        }

        Long scheduleId = request.getScheduleId();
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new IllegalArgumentException("Schedule not found"));

        int count = request.getPassengers().size();

        // Atomic seat decrement - if result is 0 rows updated, not enough seats
        int updated = scheduleRepository.decrementAvailableSeats(scheduleId, count);
        if (updated == 0) {
            throw new IllegalStateException("Not enough available seats");
        }

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setSchedule(schedule);
        booking.setBookingDate(LocalDateTime.now());
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setReferenceCode(generateReferenceCode());
        booking = bookingRepository.save(booking);

        // Create passengers
        for (PassengerDTO p : request.getPassengers()) {
            Passenger passenger = new Passenger();
            passenger.setBooking(booking);
            passenger.setName(p.getName());
            passenger.setAge(p.getAge());
            passengerRepository.save(passenger);
            booking.getPassengers().add(passenger);
        }

        BigDecimal totalPrice = schedule.getPrice().multiply(BigDecimal.valueOf(count));
        return new BookingResponse(
                booking.getId(),
                booking.getReferenceCode(),
                schedule.getId(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getPassengers().stream().map(pass -> {
                    PassengerDTO dto = new PassengerDTO();
                    dto.setName(pass.getName());
                    dto.setAge(pass.getAge());
                    return dto;
                }).collect(Collectors.toList()),
                totalPrice);
    }

    @Transactional(readOnly = true)
    public Page<BookingSummaryDTO> listMyBookings(int page, int size) {
        User user = getCurrentUser();
        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size),
                Sort.by(Sort.Direction.DESC, "bookingDate"));
        Page<Booking> bookingPage = bookingRepository.findByUserId(user.getId(), pageable);
        List<BookingSummaryDTO> content = bookingPage.getContent().stream().map(b -> {
            BookingSummaryDTO dto = new BookingSummaryDTO();
            dto.setBookingId(b.getId());
            dto.setReferenceCode(b.getReferenceCode());
            dto.setScheduleId(b.getSchedule().getId());
            dto.setBookingDate(b.getBookingDate());
            dto.setStatus(b.getStatus());
            dto.setPassengersCount(b.getPassengers().size());
            dto.setTotalPrice(b.getSchedule().getPrice().multiply(BigDecimal.valueOf(b.getPassengers().size())));
            return dto;
        }).collect(Collectors.toList());
        return new PageImpl<>(content, pageable, bookingPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public BookingResponse getMyBookingByRef(String referenceCode) {
        User user = getCurrentUser();
        Booking booking = bookingRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Booking not found");
        }
        int count = booking.getPassengers().size();
        BigDecimal totalPrice = booking.getSchedule().getPrice().multiply(BigDecimal.valueOf(count));
        return new BookingResponse(
                booking.getId(),
                booking.getReferenceCode(),
                booking.getSchedule().getId(),
                booking.getBookingDate(),
                booking.getStatus(),
                booking.getPassengers().stream().map(pass -> {
                    PassengerDTO dto = new PassengerDTO();
                    dto.setName(pass.getName());
                    dto.setAge(pass.getAge());
                    return dto;
                }).collect(Collectors.toList()),
                totalPrice);
    }

    @Transactional
    public void cancelMyBooking(String referenceCode) {
        User user = getCurrentUser();
        Booking booking = bookingRepository.findByReferenceCode(referenceCode)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
        if (!booking.getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Booking not found");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return; // idempotent
        }

        // Trigger refund if applicable
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            paymentService.refundPayment(booking);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        scheduleRepository.incrementAvailableSeats(booking.getSchedule().getId(), booking.getPassengers().size());
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new IllegalStateException("Unauthenticated");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private String generateReferenceCode() {
        // Try a few times in the unlikely event of a collision
        for (int i = 0; i < 5; i++) {
            byte[] bytes = new byte[12]; // 12 random bytes -> 24 hex chars
            secureRandom.nextBytes(bytes);
            String ref = HexFormat.of().formatHex(bytes).toUpperCase();
            if (bookingRepository.findByReferenceCode(ref).isEmpty()) {
                return ref;
            }
        }
        throw new IllegalStateException("Unable to generate unique booking reference");
    }
}
