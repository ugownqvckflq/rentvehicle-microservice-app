package com.proj.payment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "vehicle")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type", nullable = false)
    @NotBlank(message = "Vehicle Type must not be blank")
    private String vehicleType;

    @Column(name = "license_plate", nullable = false)
    @NotBlank(message = "License Plate must not be blank")
    private String licensePlate;

    @Column(nullable = false)
    @NotBlank(message = "Model must not be blank")
    private String model;

    @Column(nullable = false)
    @NotNull(message = "Speed must not be null")
    @Min(value = 0, message = "Speed must be a positive number")
    private Integer speed;

    @Column(nullable = false)
    @NotBlank(message = "Status must not be blank")
    private String status;

    @Column(name = "fuel_level", nullable = false)
    @NotNull(message = "Fuel Level must not be null")
    @Min(value = 0, message = "Fuel Level must be a positive number")
    private Integer fuelLevel;

    @Column(name = "horse_power", nullable = false)
    @NotNull(message = "Horse Power must not be null")
    @Min(value = 0, message = "Horse Power must be a positive number")
    private Integer horsePower;

    @Column(name = "number_of_doors", nullable = false)
    @NotNull(message = "Number of Doors must not be null")
    @Min(value = 1, message = "Number of Doors must be at least 1")
    private Integer numberOfDoors;

    @Column(name = "battery_level", nullable = false)
    @NotNull(message = "Battery Level must not be null")
    @Min(value = 0, message = "Battery Level must be a positive number")
    private Integer batteryLevel;
}