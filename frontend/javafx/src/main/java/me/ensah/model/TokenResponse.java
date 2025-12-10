package me.ensah.model;

/**
 * TokenResponse - Response from backend auth endpoints (/api/auth/login or /register)
 * 
 * Contains JWT token and user information
 * Jackson automatically deserializes JSON response into this object
 * 
 * Example JSON from backend:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
 *   "userId": 1,
 *   "name": "John Doe",
 *   "email": "john@example.com",
 *   "role": "admin"
 * }
 */
public class TokenResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;

    // No-arg constructor required by Jackson
    public TokenResponse() {}

    // Getters and Setters (required by Jackson)
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
