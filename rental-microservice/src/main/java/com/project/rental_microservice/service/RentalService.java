package com.project.rental_microservice.service;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RentalService {
    Rental rentVehicle(String userId, RentalRequest rentalRequest, String jwtToken);
    Rental returnVehicle(ReturnRequest returnRequest, String jwtToken);
    Rental getRentalById(Long rentalId);
    List<Rental> getRentalsByUserId(Long userId);

}
