package com.proj.payment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Table(name = "vehicle_pricing")
public class VehiclePricing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type", nullable = false)
    @NotBlank(message = "Vehicle Type must not be blank")
    private String vehicleType;

    @Column(name = "initial_cost", nullable = false)
    @NotNull(message = "Initial Cost must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Initial Cost must be positive")
    private BigDecimal initialCost;

    @Column(name = "per_minute_cost", nullable = false)
    @NotNull(message = "Per Minute Cost must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Per Minute Cost must be positive")
    private BigDecimal perMinuteCost;
}
