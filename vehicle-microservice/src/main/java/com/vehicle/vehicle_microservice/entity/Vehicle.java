package com.vehicle.vehicle_microservice.entity;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "vehicle_type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = Car.class, name = "CAR"),
        @JsonSubTypes.Type(value = Scooter.class, name = "SCOOTER")
})
@Getter
@Setter
public abstract class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Model is mandatory")
    private String model;

    @Min(value = 0, message = "Speed must be non-negative")
    private double speed;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is mandatory")
    private Status status;

    @NotBlank(message = "License plate is mandatory")
    private String licensePlate;

    @Column(insertable = false, updatable = false)
    private String vehicle_type;
}
