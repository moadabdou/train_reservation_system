package com.ensah.train_reservation_system.DTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// For the Response
@Getter
@RequiredArgsConstructor
public class AuthResponse {
    private final String token;
}