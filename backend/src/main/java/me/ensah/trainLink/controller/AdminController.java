package me.ensah.trainLink.controller;

import me.ensah.trainLink.model.Booking;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.BookingRepository;
import me.ensah.trainLink.repository.StationRepository;
import me.ensah.trainLink.repository.TrainRepository;
import me.ensah.trainLink.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * AdminController - Handles all admin-related API endpoints
 * 
 * Provides endpoints for admin dashboard:
 * - Dashboard statistics (total users, bookings, trains, etc.)
 * - User/Client management (list all users)
 * 
 * All endpoints are protected to ensure only admins can access them
 */
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private StationRepository stationRepository;

    /**
     * GET /api/admin/dashboard-stats
     * Returns dashboard statistics
     */
    @GetMapping("/dashboard-stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        stats.put("totalUsers", userRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalTrains", trainRepository.count());
        stats.put("totalStations", stationRepository.count());

        return ResponseEntity.ok(stats);
    }

    /**
     * GET /api/admin/clients
     * Returns a list of all clients with pagination
     * 
     * Query params:
     * - page: page number (0-indexed), default 0
     * - size: page size, default 10
     * - search: optional search term for name or email
     */
    @GetMapping("/clients")
    public ResponseEntity<Map<String, Object>> getAllClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage;

        if (search != null && !search.isEmpty()) {
            // Search by name or email
            String searchLower = search.toLowerCase();
            List<User> filteredUsers = userRepository.findAll().stream()
                    .filter(u -> u.getName().toLowerCase().contains(searchLower) ||
                            u.getEmail().toLowerCase().contains(searchLower))
                    .skip((long) page * size)
                    .limit(size)
                    .collect(Collectors.toList());

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("clients", filteredUsers.stream()
                    .map(this::buildClientInfo)
                    .collect(Collectors.toList()));
            response.put("totalElements", filteredUsers.size());
            response.put("totalPages", (filteredUsers.size() + size - 1) / size);
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ResponseEntity.ok(response);
        } else {
            // Get all users paginated
            usersPage = userRepository.findAll(pageable);

            Map<String, Object> response = new LinkedHashMap<>();
            response.put("clients", usersPage.getContent().stream()
                    .map(this::buildClientInfo)
                    .collect(Collectors.toList()));
            response.put("totalElements", usersPage.getTotalElements());
            response.put("totalPages", usersPage.getTotalPages());
            response.put("currentPage", page);
            response.put("pageSize", size);

            return ResponseEntity.ok(response);
        }
    }

    /**
     * GET /api/admin/clients/{id}
     * Returns detailed information about a specific client
     */
    @GetMapping("/clients/{id}")
    public ResponseEntity<Map<String, Object>> getClientDetails(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        Map<String, Object> details = new LinkedHashMap<>();
        details.put("id", user.getId());
        details.put("name", user.getName());
        details.put("email", user.getEmail());
        details.put("role", user.getRole());

        // Get user's bookings
        List<Booking> userBookings = bookingRepository.findByUserId(id);
        details.put("totalBookings", userBookings.size());
        details.put("confirmedBookings", userBookings.stream()
                .filter(b -> "CONFIRMED".equals(b.getStatus()))
                .count());

        return ResponseEntity.ok(details);
    }

    /**
     * Helper method to build client information map
     */
    private Map<String, Object> buildClientInfo(User user) {
        Map<String, Object> clientInfo = new LinkedHashMap<>();
        clientInfo.put("id", user.getId());
        clientInfo.put("name", user.getName());
        clientInfo.put("email", user.getEmail());
        clientInfo.put("createdAt", new Date()); // Note: Add createdAt field to User model if needed

        // Get booking statistics for this user
        List<Booking> userBookings = bookingRepository.findByUserId(user.getId());
        clientInfo.put("totalBookings", userBookings.size());

        return clientInfo;
    }
}
