package com.proj.payment_service.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "rentals")
@Getter
@Setter
public class Rental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    @NotNull(message = "User ID must not be null")
    private Long userId;

    @Column(name = "vehicle_id", nullable = false)
    @NotNull(message = "Vehicle ID must not be null")
    private Long vehicleId;

    @Column(name = "start_time", nullable = false)
    @NotNull(message = "Start Time must not be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Transient // Не будуте сохраняться в бд
    @NotBlank(message = "Duration must not be blank")
    private String duration;

    @Transient // Не будуте сохраняться в бд
    @NotBlank(message = "Vehicle Type must not be blank")
    private String vehicleType;


}