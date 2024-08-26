package com.project.notification_service.entity;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "vehicle_type", visible = true)
@Getter
@Setter
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Model must not be blank")
    @Size(max = 50, message = "Model should be less than or equal to 50 characters")
    private String model;

    @Positive(message = "Speed must be a positive number")
    private double speed;

    @NotNull(message = "Status must not be null")
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotBlank(message = "License Plate must not be blank")
    private String licensePlate;

    @Column(insertable = false, updatable = false)
    private String vehicle_type;
}