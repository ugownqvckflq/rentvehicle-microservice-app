package com.project.rental_microservice.service;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.entity.RentalRequest;
import com.project.rental_microservice.entity.ReturnRequest;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.restTemplate.service.VehicleServiceClient;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RentalServiceImpl implements RentalService {

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private VehicleServiceClient vehicleServiceClient;

    @Override
    public Rental rentVehicle(RentalRequest rentalRequest) {
        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(rentalRequest.getLicensePlate());
        if (vehicle != null) {
            Rental rental = new Rental();
            rental.setUserId(rentalRequest.getUserId());
            rental.setVehicleId(vehicle.getId());
            rental.setStartTime(LocalDateTime.now());

            // Сохранение в базу данных
            rental = rentalRepository.save(rental);

            // Вычисление продолжительности и обновление объекта
            rental.setDuration(calculateDuration(rental.getStartTime(), rental.getEndTime()));

            return rental;
        } else {
            throw new RuntimeException("Vehicle not found with license plate: " + rentalRequest.getLicensePlate());
        }
    }


    @Override
    public Rental returnVehicle(ReturnRequest returnRequest) {
        Optional<Rental> rentalOpt = rentalRepository.findById(returnRequest.getRentalId());
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            rental.setEndTime(LocalDateTime.now());

            // Сохранение в базу данных
            rental = rentalRepository.save(rental);

            // Вычисление продолжительности и обновление объекта
            rental.setDuration(calculateDuration(rental.getStartTime(), rental.getEndTime()));

            return rental;
        } else {
            throw new RuntimeException("Rental not found with id: " + returnRequest.getRentalId());
        }
    }

    private String calculateDuration(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime != null && endTime != null) {
            long seconds = ChronoUnit.SECONDS.between(startTime, endTime);
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            long secs = seconds % 60;
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        }
        return null;
    }

    @Override
    public Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId).orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));
    }
}