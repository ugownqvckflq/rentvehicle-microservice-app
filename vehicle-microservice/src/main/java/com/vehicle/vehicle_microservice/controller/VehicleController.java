package com.vehicle.vehicle_microservice.controller;


import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Scooter;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;
import com.vehicle.vehicle_microservice.services.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vehicles")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/all")
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicle();
        return ResponseEntity.ok(vehicles);
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
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/plate/{plate}")
    public ResponseEntity<Vehicle> getByPlate(@PathVariable String plate) {
        Optional<Vehicle> vehicle = vehicleService.getByPlate(plate);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody Vehicle vehicleDetails) {
        Optional<Vehicle> vehicleOptional = vehicleService.getById(id);
        if (vehicleOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Vehicle vehicle = vehicleOptional.get();
        vehicle.setModel(vehicleDetails.getModel());
        vehicle.setSpeed(vehicleDetails.getSpeed());
        vehicle.setStatus(vehicleDetails.getStatus());
        vehicle.setLicensePlate(vehicleDetails.getLicensePlate());

        if (vehicle instanceof Car && vehicleDetails instanceof Car) {
            Car car = (Car) vehicle;
            Car carDetails = (Car) vehicleDetails;
            car.setFuelLevel(carDetails.getFuelLevel());
            car.setHorsePower(carDetails.getHorsePower());
            car.setNumberOfDoors(carDetails.getNumberOfDoors());
        } else if (vehicle instanceof Scooter && vehicleDetails instanceof Scooter) {
            Scooter scooter = (Scooter) vehicle;
            Scooter scooterDetails = (Scooter) vehicleDetails;
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

    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<String> handleDuplicateLicensePlateException(DuplicateLicensePlateException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
}
