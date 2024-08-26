package com.vehicle.vehicle_microservice.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import lombok.*;

@Entity
@DiscriminatorValue("CAR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car extends Vehicle {
    @Min(value = 0, message = "Fuel level must be non-negative")
    private double fuelLevel;

    @Min(value = 1, message = "Horsepower must be at least 1")
    private int horsePower;

    @Min(value = 1, message = "There must be at least 1 door")
    private int numberOfDoors;
}


