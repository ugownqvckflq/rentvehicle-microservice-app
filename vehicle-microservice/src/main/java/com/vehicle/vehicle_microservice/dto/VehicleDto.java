package com.vehicle.vehicle_microservice.dto;

import com.vehicle.vehicle_microservice.entity.Status;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehicleDto {

    @NotBlank(message = "Model is required")
    private String model;

    @Positive(message = "Speed must be positive")
    private double speed;

    @NotBlank(message = "License plate is required")
    @Pattern(regexp = "^[A-Z0-9-]+$", message = "License plate must contain only uppercase letters, numbers, and dashes")
    private String licensePlate;

    @NotBlank(message = "Vehicle type is required")
    @Pattern(regexp = "^(CAR|SCOOTER)$", message = "Vehicle type must be either 'CAR' or 'SCOOTER'")
    private String vehicleType;

    @PositiveOrZero(message = "Fuel level must be zero or positive")
    private double fuelLevel;

    @PositiveOrZero(message = "Horse power must be zero or positive")
    private int horsePower;

    @PositiveOrZero(message = "Number of doors must be zero or positive")
    private int numberOfDoors;

    @PositiveOrZero(message = "Battery level must be zero or positive")
    private int batteryLevel;

    @NotNull(message = "Status is required")
    private Status status;
}
