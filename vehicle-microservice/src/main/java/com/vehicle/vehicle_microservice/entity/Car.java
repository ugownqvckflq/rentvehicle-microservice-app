package com.vehicle.vehicle_microservice.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

@Entity
@DiscriminatorValue("CAR")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Car extends Vehicle {
    private double fuelLevel;
    private int horsePower;
    private int numberOfDoors;
}
