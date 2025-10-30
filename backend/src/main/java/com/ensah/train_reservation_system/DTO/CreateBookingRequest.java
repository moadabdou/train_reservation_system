package com.ensah.train_reservation_system.DTO;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateBookingRequest {
    @NotNull
    private Long scheduleId;

    @NotEmpty
    @Valid
    private List<@NotNull PassengerDTO> passengers;
}
