package com.project.rental_microservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
public class RentalRequest {
    @NotBlank(message = "License plate is required")
    private String licensePlate;
}
