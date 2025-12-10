package me.ensah.services;

import com.fasterxml.jackson.core.type.TypeReference;

import me.ensah.config.Config;
import me.ensah.config.Session;
import me.ensah.model.TokenResponse;
import me.ensah.net.ApiClient;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AuthService {
    private final ApiClient api;

    public AuthService(ApiClient api) {
        this.api = api;
    }

    public static AuthService defaultInstance() {
        return new AuthService(new ApiClient(Config.apiBaseUrl()));
    }

    public String register(String name, String email, String password) throws IOException, InterruptedException {
        // STEP 1: Prepare request body with user details
        Map<String, String> body = new HashMap<>();
        body.put("name", name);
        body.put("email", email);
        body.put("password", password);

        // STEP 2: Send POST to /api/auth/register
        TokenResponse res = api.post("/auth/register", body, new TypeReference<TokenResponse>() {
        });

        // STEP 3: Check if registration successful
        if (res != null && res.getToken() != null) {
            // STEP 4: Store token in Session
            Session.setToken(res.getToken());

            // STEP 5: Store user information in Session
            me.ensah.model.User user = new me.ensah.model.User(
                    res.getUserId(),
                    res.getName(),
                    res.getEmail(),
                    res.getRole());
            Session.setCurrentUser(user);

            return res.getToken();
        }
        throw new IOException("No token returned from register");
    }

    public String login(String email, String password) throws IOException, InterruptedException {
        // STEP 1: Prepare the request body - create a Map with email and password
        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        // STEP 2: Send POST request to backend at /auth/login
        // ApiClient will convert Map to JSON:
        // {"email":"user@test.com","password":"pass123"}
        // Backend will return TokenResponse with token + user info
        TokenResponse res = api.post("/auth/login", body, new TypeReference<TokenResponse>() {
        });

        // STEP 3: Check if we got a valid response
        if (res != null && res.getToken() != null) {
            // STEP 4: Save the JWT token in Session (in-memory storage)
            // This token will be used for all future authenticated requests
            Session.setToken(res.getToken());

            // STEP 5: Create User object and save in Session
            // This allows us to check user role without additional API calls
            me.ensah.model.User user = new me.ensah.model.User(
                    res.getUserId(),
                    res.getName(),
                    res.getEmail(),
                    res.getRole());
            Session.setCurrentUser(user);

            // DEBUG: Log user details
            System.out.println("=== LOGIN SUCCESS ===");
            System.out.println("User ID: " + user.getId());
            System.out.println("Name: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Role: " + user.getRole());
            System.out.println("Is Admin: " + user.isAdmin());
            System.out.println("=====================");

            return res.getToken();
        }

        // STEP 6: If no token received, throw error
        throw new IOException("No token returned from login");
    }
}
