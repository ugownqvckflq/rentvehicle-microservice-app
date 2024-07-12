package com.vehicle.vehicle_microservice.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("SCOOTER")
@RequiredArgsConstructor
@Setter
@Getter
public class Scooter extends Vehicle {
    private int batteryLevel;
}
