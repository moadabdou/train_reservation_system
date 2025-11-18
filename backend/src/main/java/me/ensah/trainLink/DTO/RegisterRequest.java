package me.ensah.trainLink.DTO;

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
