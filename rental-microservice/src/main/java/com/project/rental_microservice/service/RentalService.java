package com.project.rental_microservice.service;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import jakarta.servlet.http.HttpServletRequest;

public interface RentalService {
    Rental rentVehicle(HttpServletRequest request, RentalRequest rentalRequest);
    Rental returnVehicle(ReturnRequest returnRequest);
    Rental getRentalById(Long rentalId);

}
