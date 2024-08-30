package com.vehicle.vehicle_microservice.services;

import com.vehicle.vehicle_microservice.dto.VehicleCreateDTO;
import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Scooter;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;
import com.vehicle.vehicle_microservice.repository.VehicleRepository;


import com.vehicle.vehicle_microservice.services.VehicleService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;


    @Override
    public List<Vehicle> getAllVehicle() {
        return vehicleRepository.findAll();
    }

    @Override
    public Optional<Vehicle> getById(Long id) {
        return vehicleRepository.findById(id);
    }

    @Override
    @Transactional
    public Vehicle createVehicle(VehicleCreateDTO vehicleCreateDTO) {
        Vehicle vehicle;

        switch (vehicleCreateDTO.getVehicleType()) {
            case "CAR" -> {
                Car car = new Car();
                car.setModel(vehicleCreateDTO.getModel());
                car.setSpeed(vehicleCreateDTO.getSpeed());
                car.setLicensePlate(vehicleCreateDTO.getLicensePlate());
                car.setFuelLevel(vehicleCreateDTO.getFuelLevel());
                car.setHorsePower(vehicleCreateDTO.getHorsePower());
                car.setNumberOfDoors(vehicleCreateDTO.getNumberOfDoors());
                car.setStatus(Status.AVAILABLE);
                car.setVehicle_type("CAR");
                vehicle = car;
            }
            case "SCOOTER" -> {
                Scooter scooter = new Scooter();
                scooter.setModel(vehicleCreateDTO.getModel());
                scooter.setSpeed(vehicleCreateDTO.getSpeed());
                scooter.setLicensePlate(vehicleCreateDTO.getLicensePlate());
                scooter.setBatteryLevel(vehicleCreateDTO.getBatteryLevel());
                scooter.setStatus(Status.AVAILABLE);
                scooter.setVehicle_type("SCOOTER");
                vehicle = scooter;
            }
            default -> throw new IllegalArgumentException("Invalid vehicle type: " + vehicleCreateDTO.getVehicleType());
        }

        try {
            return vehicleRepository.save(vehicle);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicateLicensePlateException("Vehicle with license plate " + vehicle.getLicensePlate() + " already exists.");
        }
    }

    @Override
    public void saveVehicle(Vehicle vehicle) {
        vehicleRepository.save(vehicle);
    }

    @Override
    public Vehicle updateVehicle(Vehicle existingVehicle, VehicleCreateDTO vehicleCreateDTO) {
        existingVehicle.setModel(vehicleCreateDTO.getModel());
        existingVehicle.setSpeed(vehicleCreateDTO.getSpeed());
        existingVehicle.setLicensePlate(vehicleCreateDTO.getLicensePlate());
        existingVehicle.setStatus(vehicleCreateDTO.getStatus());

        if (existingVehicle instanceof Car car && vehicleCreateDTO.getVehicleType().equals("CAR")) {
            car.setFuelLevel(vehicleCreateDTO.getFuelLevel());
            car.setHorsePower(vehicleCreateDTO.getHorsePower());
            car.setNumberOfDoors(vehicleCreateDTO.getNumberOfDoors());
        } else if (existingVehicle instanceof Scooter scooter && vehicleCreateDTO.getVehicleType().equals("SCOOTER")) {
            scooter.setBatteryLevel(vehicleCreateDTO.getBatteryLevel());
        }

        return vehicleRepository.save(existingVehicle);
    }

    @Override
    public void deleteVehicle(Long id) {
        vehicleRepository.deleteById(id);
    }

    @Override
    public Optional<Vehicle> getByPlate(String plate) {
        return vehicleRepository.findByLicensePlate(plate);
    }

    @Override
    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(Status.AVAILABLE);
    }

    @Override
    public List<Vehicle> searchVehiclesByModel(String model) {
        return vehicleRepository.findByModel(model);
    }

    @Override
    public List<Vehicle> filterVehicles(String model, Double minFuelLevel, Double maxFuelLevel, Integer minBatteryLevel, Integer maxBatteryLevel, Status status) {
        if (model != null && status != null) {
            return vehicleRepository.findByModel(model);
        }

        if (status != null) {
            return vehicleRepository.findByStatus(status);
        }

        if (minFuelLevel != null && maxFuelLevel != null) {
            return vehicleRepository.findAvailableCarsByFuelLevelBetween(minFuelLevel, maxFuelLevel);
        }

        if (minBatteryLevel != null && maxBatteryLevel != null) {
            return vehicleRepository.findAvailableScootersByBatteryLevelBetween(minBatteryLevel, maxBatteryLevel);
        }

        return List.of();
    }

}