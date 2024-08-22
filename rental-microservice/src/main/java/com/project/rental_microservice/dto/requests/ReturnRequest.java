package com.project.rental_microservice.dto.requests;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ReturnRequest {
    @NotBlank(message = "License plate is required")
    private String licensePlate;
}
