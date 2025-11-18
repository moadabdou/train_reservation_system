package me.ensah.trainLink.DTO;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * AuthResponse - Response DTO for login/register endpoints
 * 
 * Returns JWT token AND user information to the frontend
 * This allows the frontend to know the user's role immediately after login
 * without making an additional API call
 * 
 * LEARNING NOTE:
 * - DTO (Data Transfer Object): Objects used to transfer data between layers
 * - Lombok @Getter: Automatically generates getter methods
 * - Lombok @AllArgsConstructor: Generates constructor with all fields
 * - Lombok @NoArgsConstructor: Generates no-arg constructor (required for JSON deserialization)
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;      // JWT token for authentication
    private Long userId;       // User's unique ID
    private String name;       // User's full name
    private String email;      // User's email
    private String role;       // User's role: "admin" or "client"
}