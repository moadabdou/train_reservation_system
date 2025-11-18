package me.ensah.trainLink.DTO;

import lombok.Getter;
import lombok.Setter;

// For Login
@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
}