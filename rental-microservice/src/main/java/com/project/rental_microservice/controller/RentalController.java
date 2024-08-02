package com.project.rental_microservice.controller;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.service.RentalService;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalController {


    private final RentalService rentalService;


    private final VehicleServiceClient vehicleServiceClient;


    @PostMapping("/rent")
    public ResponseEntity<?> rentVehicle(@RequestHeader("X-User-Id") String userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody RentalRequest rentalRequest) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        try {
            Rental rental = rentalService.rentVehicle(userId, rentalRequest, jwtToken);
            return ResponseEntity.ok(rental);
        } catch (WebClientResponseException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при аренде транспортного средства");
        }
    }

    @PostMapping("/return")
    public ResponseEntity<?> returnVehicle(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody ReturnRequest returnRequest) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        try {
            Rental rental = rentalService.returnVehicle(returnRequest, jwtToken);
            return ResponseEntity.ok(rental);
        } catch (WebClientResponseException e) {
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при возврате транспортного средства");
        }
    }

    @GetMapping("/hello")
    public ResponseEntity<?> hello(@RequestHeader("X-User-Id") String userId, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String jwtToken = authorizationHeader.replace("Bearer ", "");

        try {
            VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate("SCOOT3", jwtToken).block();
            return ResponseEntity.ok(vehicle);
        } catch (WebClientResponseException e) {
            // Логирование ошибки
            e.printStackTrace();
            return ResponseEntity.status(e.getRawStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            // Логирование общей ошибки
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при получении данных о транспортном средстве");
        }
    }




    @GetMapping("/{rentalId}")
    public ResponseEntity<Rental> getRentalById(@RequestHeader("X-User-Role") String userRole, @PathVariable Long rentalId) {
        if ("ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.ok(rentalService.getRentalById(rentalId));
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


    }

    @GetMapping("/user/{userId}")
    public List<Rental> getRentalsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rentalService.getRentalsByUserId(userId)).getBody();
    }

}