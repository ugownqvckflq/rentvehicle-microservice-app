package com.vehicle.vehicle_microservice.controller;


import com.example.rolecheck.RoleCheck;
import com.vehicle.vehicle_microservice.entity.*;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;
import com.vehicle.vehicle_microservice.services.VehicleService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    private final VehicleService vehicleService;


    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;

    }


    //RoleCheck("ROLE_ADMIN") //доделать
    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles(@RequestHeader("X-User-Role") String userRole) {
        // Log received user role
        System.out.println("Received User Role: " + userRole);

        if ("ROLE_ADMIN".equals(userRole)) {
            List<Vehicle> vehicles = vehicleService.getAllVehicle();
            return ResponseEntity.ok(vehicles);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getById(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        Vehicle savedVehicle = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(savedVehicle);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@RequestHeader("X-User-Role") String userRole, @PathVariable Long id) {

        if ("ROLE_ADMIN".equals(userRole)) {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.noContent().build();
        }else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Vehicle> getByPlate(@PathVariable String plate) {
        Optional<Vehicle> vehicle = vehicleService.getByPlate(plate);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@RequestHeader("X-User-Role") String userRole, @PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        if (!"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Optional<Vehicle> vehicleOptional = vehicleService.getById(id);
        if (vehicleOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle vehicle = vehicleOptional.get();
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setSpeed(vehicleDetails.getSpeed());
        vehicle.setStatus(vehicleDetails.getStatus());
        vehicle.setLicensePlate(vehicleDetails.getLicensePlate());

        if (vehicle instanceof Car car && vehicleDetails instanceof Car carDetails) {
            car.setFuelLevel(carDetails.getFuelLevel());
            car.setHorsePower(carDetails.getHorsePower());
            car.setNumberOfDoors(carDetails.getNumberOfDoors());
        } else if (vehicle instanceof Scooter scooter && vehicleDetails instanceof Scooter scooterDetails) {
            scooter.setBatteryLevel(scooterDetails.getBatteryLevel());
        }

        Vehicle updatedVehicle = vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(updatedVehicle);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(availableVehicles);
    }

    @PostMapping("/set-status/{id}/{status}")
    public ResponseEntity<Vehicle> setVehicleStatusById(@PathVariable Long id, @PathVariable Status status) {

        Vehicle vehicle = vehicleService.getById(id).orElseThrow();
        vehicle.setStatus(status);
        vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(vehicle);
    }

    @GetMapping("/search/{model}")
    public ResponseEntity<List<Vehicle>> searchVehiclesByModel(@PathVariable String model) {
        List<Vehicle> vehicles = vehicleService.searchVehiclesByModel(model);
        if (vehicles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicles);
    }

    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<String> handleDuplicateLicensePlateException(DuplicateLicensePlateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
