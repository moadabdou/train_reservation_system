package me.ensah.config;

import me.ensah.model.Schedule;
import me.ensah.model.User;

/**
 * Session - In-memory session holder for the JavaFX app
 * 
 * This class acts as a "global state manager" for the logged-in user.
 * It stores:
 * - JWT token: Used for authenticating API requests to the backend
 * - User object: Contains user details (name, email, role)
 * - Selected schedule: Temporary data for the booking flow
 * 
 * LEARNING NOTES:
 * - This is a Singleton pattern (only one instance exists)// because of static fields/methods 
 * - 'volatile' keyword: Ensures changes are visible across threads
 * - 'static' fields: Shared across entire application
 * 
 * IMPORTANT: This stores data in memory only!// no persistence or disk storage or database because of static fields
 * When app closes, all data is lost. For persistence, you'd use:
 * - Preferences API (for simple data)
 * - Local database (SQLite)
 * - Encrypted file storage
 */

// we first need to understand the concept of thread safety in Java
//thread is a lightweight subprocess, the smallest unit of processing that can be scheduled by an operating system.
//computer processor is like a chef in a kitchen, preparing and executing tasks (threads) to get things done efficiently.
//a pc can have multiple chefs (cores) working simultaneously, each handling different tasks (threads) to speed up overall performance.
//a threa
public final class Session {
    // JWT token from backend (used in Authorization header)
    private static volatile String token;// volatile to ensure visibility across threads it means that when one thread changes the value of token, other threads will see the updated value immediately.
    
    // Currently logged-in user (fetched after login)
    private static volatile User currentUser;
    
    // Temporary: selected schedule during booking process
    private static volatile Schedule selectedSchedule;

    // Private constructor prevents instantiation
    private Session() {}

    // ==================== TOKEN MANAGEMENT ====================
    
    /**
     * Stores the JWT token received from backend after login
     * @param t JWT token string
     */
    public static void setToken(String t) { 
        token = t; 
    }
    
    /**
     * Gets the stored JWT token
     * @return JWT token or null if not logged in
     */
    public static String getToken() { 
        return token; 
    }
    
    /**
     * Checks if user is authenticated (has valid token)
     * @return true if token exists and is not empty
     */
    public static boolean isAuthenticated() { 
        return token != null && !token.isBlank(); 
    }
    
    /**
     * Alias for isAuthenticated() - more semantic naming
     */
    public static boolean isLoggedIn() { 
        return isAuthenticated(); 
    }
    
    // ==================== USER MANAGEMENT ====================
    
    /**
     * Stores the current user's information
     * Called after successful login with user data from backend
     * @param user User object with id, name, email, role
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
    }
    
    /**
     * Gets the current logged-in user
     * @return User object or null if not logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if the logged-in user has admin role
     * @return true if user is admin, false otherwise
     */
    public static boolean isAdmin() {
        // Check if user exists and has admin role
        return currentUser != null && currentUser.isAdmin();
    }
    
    /**
     * Gets the current user's name
     * @return User name or "Guest" if not logged in
     */
    public static String getUserName() {
        return currentUser != null ? currentUser.getName() : "Guest";
    }
    
    /**
     * Gets the current user's email
     * @return User email or null if not logged in
     */
    public static String getUserEmail() {
        return currentUser != null ? currentUser.getEmail() : null;
    }
    
    // ==================== BOOKING FLOW MANAGEMENT ====================
    
    /**
     * Stores the schedule selected by user for booking
     * Used to pass data between MainView → BookingView
     */
    public static void setSelectedSchedule(Schedule s) { 
        selectedSchedule = s; 
    }
    
    /**
     * Gets the selected schedule for booking
     */
    public static Schedule getSelectedSchedule() { 
        return selectedSchedule; 
    }
    
    // ==================== SESSION CLEANUP ====================
    
    /**
     * Clears all session data (logout)
     * Removes token, user, and temporary data
     */
    public static void clear() { 
        token = null; 
        currentUser = null;
        selectedSchedule = null; 
    }
}
