package me.ensah.trainLink.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import me.ensah.trainLink.DTO.AuthRequest;
import me.ensah.trainLink.DTO.AuthResponse;
import me.ensah.trainLink.DTO.RegisterRequest;
import me.ensah.trainLink.model.User;
import me.ensah.trainLink.services.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        // Create new user with "client" role by default
        User newUser = new User(null, request.getName(), request.getEmail(), request.getPassword(), "client");
        
        // Register user and get JWT token
        String token = authService.register(newUser);
        
        // Get the saved user (with ID) from database
        User savedUser = authService.getUserByEmail(request.getEmail());
        
        // Return token + user info in response
        return ResponseEntity.ok(new AuthResponse(
            token,
            savedUser.getId(),
            savedUser.getName(),
            savedUser.getEmail(),
            savedUser.getRole()
        ));
    }

    @PostMapping("/login")  // This handles POST requests to /api/auth/login
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // STEP 1: Extract email and password from request body (JSON automatically converted to AuthRequest object)
        // Request body example: {"email":"user@test.com","password":"pass123"}
        
        // STEP 2: Call AuthService to validate credentials and generate JWT token
        String token = authService.login(request.getEmail(), request.getPassword());
        
        // STEP 3: Get user details from database
        User user = authService.getUserByEmail(request.getEmail());
        
        // STEP 4: Return token + user info in response (AuthResponse will be converted to JSON)
        // Response: {"token":"eyJ...","userId":1,"name":"John","email":"john@test.com","role":"admin"}
        return ResponseEntity.ok(new AuthResponse(
            token,
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getRole()
        ));
    }
}