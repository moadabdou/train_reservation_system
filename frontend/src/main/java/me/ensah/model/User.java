package me.ensah.model;

/**
 * User Model (Frontend)
 * 
 * Represents a user in the frontend application.
 * This mirrors the User entity from the backend.
 * 
 * LEARNING NOTES:
 * - This is a POJO (Plain Old Java Object) - just a data container
 * - Used to deserialize JSON responses from the backend API
 * - Jackson library converts JSON → User object automatically
 * 
 * Example JSON from backend:
 * {
 * "id": 1,
 * "name": "John Doe",
 * "email": "john@example.com",
 * "role": "admin"
 * }
 */
public class User {

    // User fields - must match backend User model field names
    private Long id;
    private String name;
    private String email;
    private String role; // "admin" or "client"

    // Additional fields for admin table display
    private String createdAt;
    private Integer totalBookings;

    // ==================== CONSTRUCTORS ====================

    /**
     * No-arg constructor required by Jackson for JSON deserialization
     */
    public User() {
    }

    /**
     * Full constructor for creating User objects manually
     */
    public User(Long id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    /**
     * Extended constructor for admin table display
     */
    public User(Long id, String name, String email, String role, String createdAt, Integer totalBookings) {
        this(id, name, email, role);
        this.createdAt = createdAt;
        this.totalBookings = totalBookings;
    }

    // ==================== GETTERS AND SETTERS ====================
    // Required by Jackson for JSON serialization/deserialization

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(Integer totalBookings) {
        this.totalBookings = totalBookings;
    }

    // ==================== UTILITY METHODS ====================

    /**
     * Checks if this user has admin role
     * 
     * @return true if user is admin, false otherwise
     */
    public boolean isAdmin() {
        return "admin".equalsIgnoreCase(role);
    }

    /**
     * Checks if this user has client role
     * 
     * @return true if user is client, false otherwise
     */
    public boolean isClient() {
        return "client".equalsIgnoreCase(role);
    }

    /**
     * String representation of User (useful for debugging)
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
