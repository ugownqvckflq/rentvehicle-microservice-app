package com.project.rental_microservice.service;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.exceptions.InvalidUserIdFormatException;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface RentalService {
    Rental rentVehicle(Long userId, RentalRequest rentalRequest, String jwtToken);
    Rental returnVehicle(ReturnRequest returnRequest, String jwtToken);
    Rental getRentalById(Long rentalId);
    List<Rental> getRentalsByUserId(Long userId);

    static Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("User ID is missing in the request headers");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new InvalidUserIdFormatException("Invalid user ID format");
        }
    }
}
