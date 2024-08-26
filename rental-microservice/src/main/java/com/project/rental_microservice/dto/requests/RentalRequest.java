package com.project.rental_microservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
public class RentalRequest {
    @NotBlank(message = "License plate is required")
    private String licensePlate;
}
