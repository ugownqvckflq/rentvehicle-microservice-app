package com.project.rental_microservice.service;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.restTemplate.service.VehicleServiceClient;
import com.project.rental_microservice.security.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final RentalRepository rentalRepository;
    private final VehicleServiceClient vehicleServiceClient;
    private final KafkaProducerService kafkaProducerService;


    //TODO почему то не работает изменение статуса транспорта
    @Override
    public Rental rentVehicle(HttpServletRequest request, RentalRequest rentalRequest) {
        Long userId = (Long) request.getAttribute("userId");
        if (userId == null) {
            throw new RuntimeException("User ID is missing in request");
        }

        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(rentalRequest.getLicensePlate());

        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with license plate: " + rentalRequest.getLicensePlate());
        }
        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "UNAVAILABLE");

        if (vehicle != null) {
            Rental rental = new Rental();
            rental.setUserId(userId);
            rental.setVehicleId(vehicle.getId());
            rental.setStartTime(LocalDateTime.now());
            Rental savedRental = rentalRepository.save(rental);


            // Отправить сообщение в Kafka о новой аренде
            kafkaProducerService.sendMessage(savedRental);

            return savedRental;
        } else {
            throw new RuntimeException("Vehicle not found with license plate: " + rentalRequest.getLicensePlate());
        }
    }

    @Override
    public Rental returnVehicle(ReturnRequest returnRequest) {
        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(returnRequest.getLicensePlate());

        if (vehicle == null) {
            throw new RuntimeException("Vehicle not found with license plate: " + returnRequest.getLicensePlate());
        }
        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "AVAILABLE");

        Optional<Rental> rentalOpt = rentalRepository.findByVehicleIdAndEndTimeIsNull(vehicle.getId());
        if (rentalOpt.isPresent()) {
            Rental rental = rentalOpt.get();
            rental.setEndTime(LocalDateTime.now());
            rental.setDuration(String.valueOf(Duration.between(rental.getStartTime(), rental.getEndTime())));
            Rental updatedRental = rentalRepository.save(rental);

            // Отправить сообщение в Kafka о возврате транспорта
            kafkaProducerService.sendMessage(updatedRental);

            return updatedRental;
        } else {
            throw new RuntimeException("Rental not found for vehicle with license plate: " + returnRequest.getLicensePlate());
        }
    }

    @Override
    public Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found with id: " + rentalId));
    }
}
