package com.project.rental_microservice.controller;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.service.RentalService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;


    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(HttpServletRequest request, @RequestBody RentalRequest rentalRequest) {
        Rental rental = rentalService.rentVehicle(request, rentalRequest);
        return ResponseEntity.ok(rental);
    }


    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@RequestBody ReturnRequest returnRequest) {
        Rental rental = rentalService.returnVehicle(returnRequest);
        return ResponseEntity.ok(rental);
    }

    @PreAuthorize("hasRole('ADMIN')") //TODO сделать чтобы было так чтобы было видно что за роль у пользователя
    @GetMapping("/{rentalId}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long rentalId) {
        return ResponseEntity.ok(rentalService.getRentalById(rentalId));
    }
}