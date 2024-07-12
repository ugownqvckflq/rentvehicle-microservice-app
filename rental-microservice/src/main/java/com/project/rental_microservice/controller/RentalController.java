package com.project.rental_microservice.controller;

import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.entity.RentalRequest;
import com.project.rental_microservice.entity.ReturnRequest;
import com.project.rental_microservice.service.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(@RequestBody RentalRequest rentalRequest) {
        return ResponseEntity.ok(rentalService.rentVehicle(rentalRequest));
    }

    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@RequestBody ReturnRequest returnRequest) {
        return ResponseEntity.ok(rentalService.returnVehicle(returnRequest));
    }

    @GetMapping("/{rentalId}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long rentalId) {
        return ResponseEntity.ok(rentalService.getRentalById(rentalId));
    }
}