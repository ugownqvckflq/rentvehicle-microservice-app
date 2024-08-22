package com.project.rental_microservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class VehicleDto {
    @NotNull(message = "ID is required")
    private Long id;

    @NotBlank(message = "License plate is required")
    private String licensePlate;

    @NotBlank(message = "Status is required")
    private String status;
}
