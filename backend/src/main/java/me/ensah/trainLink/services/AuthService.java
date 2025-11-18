package me.ensah.trainLink.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import me.ensah.trainLink.model.User;
import me.ensah.trainLink.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }
        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        return jwtService.generateToken(savedUser);
    }

    public String login(String email, String password) {
        // STEP 1: Find user by email in database
        // If user not found, throw exception "Invalid email or password"
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        // STEP 2: Compare the plain text password with the hashed password in database
        // passwordEncoder.matches(plainPassword, hashedPasswordFromDB)
        // Uses BCrypt to hash the input password and compare with stored hash
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }
        
        // STEP 3: If password matches, generate and return JWT token(which is a String , that serves as authentication proof)
        // Token will contain user's email and expiration time
        return jwtService.generateToken(user);
    }
    //jwt stands for JSON Web Token which is a compact, URL-safe means of representing claims to be transferred between two parties.
    
    /**
     * Gets a user by their email address
     * Used to fetch user details after login/register
     * 
     * @param email User's email address
     * @return User object with all details
     * @throws IllegalArgumentException if user not found
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }
}
