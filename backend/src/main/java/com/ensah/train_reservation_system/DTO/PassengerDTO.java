package com.ensah.train_reservation_system.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PassengerDTO {
    @NotBlank
    private String name;

    @Min(0)
    private int age;
}
