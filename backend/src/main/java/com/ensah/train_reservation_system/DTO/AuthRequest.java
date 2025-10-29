package com.ensah.train_reservation_system.DTO;

import lombok.Getter;
import lombok.Setter;

// For Login
@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}