package com.vehicle.vehicle_microservice.services;

import com.vehicle.vehicle_microservice.dto.VehicleCreateDTO;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;

import java.util.List;
import java.util.Optional;

public interface VehicleService {
    List<Vehicle> getAllVehicle();
    Optional<Vehicle> getById(Long id);
    Vehicle createVehicle(VehicleCreateDTO vehicleCreateDTO) throws DuplicateLicensePlateException;
    void saveVehicle(Vehicle vehicle);
    Vehicle updateVehicle(Vehicle existingVehicle, VehicleCreateDTO vehicleCreateDTO);
    void deleteVehicle(Long id);
    Optional<Vehicle> getByPlate(String plate);
    List<Vehicle> getAvailableVehicles();
    List<Vehicle> searchVehiclesByModel(String model);
    List<Vehicle> filterVehicles(String model, Double minFuelLevel, Double maxFuelLevel, Integer minBatteryLevel, Integer maxBatteryLevel, Status status);

}