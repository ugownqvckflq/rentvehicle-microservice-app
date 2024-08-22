package com.project.rental_microservice.service;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;

import java.util.List;

public interface RentalService {
    Rental rentVehicle(Long userId, RentalRequest rentalRequest, String jwtToken);
    Rental returnVehicle(ReturnRequest returnRequest, String jwtToken);
    Rental getRentalById(Long rentalId);
    List<Rental> getRentalsByUserId(Long userId);
}
