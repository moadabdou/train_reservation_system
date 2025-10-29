package com.ensah.train_reservation_system.DTO;

import lombok.Getter;
import lombok.Setter;

// For Registration
@Getter 
@Setter
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}
