package com.project.rental_microservice.service;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.entity.RentalRequest;
import com.project.rental_microservice.entity.ReturnRequest;

public interface RentalService {
    Rental rentVehicle(RentalRequest rentalRequest);
    Rental returnVehicle(ReturnRequest returnRequest);
    Rental getRentalById(Long rentalId);
}
