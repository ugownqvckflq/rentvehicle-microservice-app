package com.project.rental_microservice.entity;

import lombok.Data;

@Data
public class RentalRequest {
    private Long userId;
    private String licensePlate;
}
