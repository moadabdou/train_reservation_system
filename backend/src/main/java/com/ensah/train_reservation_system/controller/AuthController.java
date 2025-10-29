package com.ensah.train_reservation_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ensah.train_reservation_system.DTO.AuthRequest;
import com.ensah.train_reservation_system.DTO.AuthResponse;
import com.ensah.train_reservation_system.DTO.RegisterRequest;
import com.ensah.train_reservation_system.model.User;
import com.ensah.train_reservation_system.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        User newUser = new User(null, request.getName(), request.getEmail(), request.getPassword());
        String token = authService.register(newUser);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(new AuthResponse(token));
    }
}