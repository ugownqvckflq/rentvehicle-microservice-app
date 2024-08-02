package com.project.rental_microservice.service;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final VehicleServiceClient vehicleServiceClient;
    private final RentalRepository rentalRepository;
    private final KafkaProducerService kafkaProducerService;


    @Override
    public Rental rentVehicle(String userId, RentalRequest rentalRequest, String jwtToken) {

        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(rentalRequest.getLicensePlate(), jwtToken).block();

        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with license plate: " + rentalRequest.getLicensePlate());
        }

        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "UNAVAILABLE", jwtToken);

        Rental rental = new Rental();
        try {
            Long userIdLong = Long.parseLong(userId);
            rental.setUserId(userIdLong);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid userId format: " + userId, e);
        }

        rental.setVehicleId(vehicle.getId());
        rental.setStartTime(LocalDateTime.now());
        Rental savedRental = rentalRepository.save(rental);

        kafkaProducerService.sendMessage(savedRental);

        return savedRental;
    }

    @Override
    public Rental returnVehicle(ReturnRequest returnRequest, String jwtToken) {
        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(returnRequest.getLicensePlate(), jwtToken).block();

        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with license plate: " + returnRequest.getLicensePlate());
        }

        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "AVAILABLE", jwtToken);

        Optional<Rental> rentalOpt = rentalRepository.findByVehicleIdAndEndTimeIsNull(vehicle.getId());
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            rental.setEndTime(LocalDateTime.now());

            Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
            rental.setDuration(formatDuration(duration));

            Rental updatedRental = rentalRepository.save(rental);

            // Отправить сообщение в Kafka о возврате транспорта
            kafkaProducerService.sendMessage(updatedRental);

            return updatedRental;
        } else {
            throw new RuntimeException("Rental not found for vehicle with license plate: " + returnRequest.getLicensePlate());
        }
    }

    private String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));
    }

    @Override
    public List<Rental> getRentalsByUserId(Long userId) {
        return rentalRepository.findByUserId(userId);
    }
}
