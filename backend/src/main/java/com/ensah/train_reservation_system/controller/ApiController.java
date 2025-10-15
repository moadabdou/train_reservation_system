package com.ensah.train_reservation_system.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ApiController {

    @GetMapping("/greeting")
    public Map<String, String> getGreeting() {
        return Map.of("message", "Hello  By MOAD ");
    }
}
