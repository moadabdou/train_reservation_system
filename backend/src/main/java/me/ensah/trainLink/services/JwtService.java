package me.ensah.trainLink.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import me.ensah.trainLink.model.User;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}") // Injects the secret from application.properties
    private String secretKey;

    public String generateToken(User user) {
        // STEP 1: Create a map to store additional user information (claims)
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());      // Store user ID
        claims.put("name", user.getName());      // Store user name
        
        // STEP 2: Build the JWT token
        return Jwts.builder()
                .claims().empty().add(claims).and()  // Add our custom claims
                .subject(user.getEmail())            // Set the main subject (user's email)
                .issuedAt(new Date(System.currentTimeMillis()))  // Set issue time (now)
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))  // Expire after 24 hours
                .signWith(getSignInKey())  // Sign the token with secret key (so it can't be tampered with)
                .compact();  // Convert to string format: "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
        
        // The token has 3 parts separated by dots:
        // HEADER.PAYLOAD.SIGNATURE
        // Example: eyJhbGc.eyJzdWI.SflKxwRJ
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ======================== Token validation helpers ========================

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        final String subject = extractUsername(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}