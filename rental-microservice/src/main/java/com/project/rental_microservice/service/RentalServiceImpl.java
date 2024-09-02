package com.project.rental_microservice.service;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.exceptions.RentalNotFoundException;
import com.project.rental_microservice.exceptions.VehicleNotFoundException;
import com.project.rental_microservice.exceptions.VehicleUnavailableException;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.config.kafka.KafkaProducerService;
import com.project.rental_microservice.service.jwt.JwtUtils;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final VehicleServiceClient vehicleServiceClient;
    private final RentalRepository rentalRepository;
    private final KafkaProducerService kafkaProducerService;



    @Override
    public Rental rentVehicle(Long userId, RentalRequest rentalRequest, String jwtToken) {
        // Получение информации о транспортном средстве по номеру
        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(rentalRequest.getLicensePlate(), jwtToken)
                .blockOptional()
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with license plate: " + rentalRequest.getLicensePlate()));

        if ("UNAVAILABLE".equals(vehicle.getStatus())) {
            throw new VehicleUnavailableException("Vehicle with license plate " + rentalRequest.getLicensePlate() + " is currently unavailable for rent.");
        }

        // Установка статуса "НЕ ДОСТУПЕН" для транспортного средства
        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "UNAVAILABLE", jwtToken);

        // Создание объекта аренды
        Rental rental = new Rental();
        rental.setUserId(userId);
        rental.setVehicleId(vehicle.getId());
        rental.setStartTime(LocalDateTime.now());

        // Сохранение информации об аренде в базе данных
        Rental savedRental = rentalRepository.save(rental);

        // Отправка сообщения в Kafka
        kafkaProducerService.sendMessage(savedRental);

        // Возврат объекта аренды
        return savedRental;
    }

    @Override
    public Rental returnVehicle(ReturnRequest returnRequest, String jwtToken) {
        // Получение информации о транспортном средстве по номеру
        VehicleDto vehicle = vehicleServiceClient.getVehicleByLicensePlate(returnRequest.getLicensePlate(), jwtToken)
                .blockOptional()
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with license plate: " + returnRequest.getLicensePlate()));

        // Поиск активной аренды для данного транспортного средства
        Rental rental = rentalRepository.findByVehicleIdAndEndTimeIsNull(vehicle.getId())
                .orElseThrow(() -> new RentalNotFoundException("Rental not found for vehicle with license plate: " + returnRequest.getLicensePlate()));

        // Получение идентификатора пользователя из токена JWT
        Long currentUserId = JwtUtils.getUserIdFromJwtToken(jwtToken);

        // Проверка, что пользователь, который пытается вернуть транспортное средство, является тем же, кто его арендовал
        if (!rental.getUserId().equals(currentUserId)) {
            throw new IllegalArgumentException("You are not authorized to return this vehicle.");
        }

        // Обновление статуса транспортного средства
        vehicleServiceClient.setVehicleStatus(vehicle.getId(), "AVAILABLE", jwtToken);

        // Установка времени окончания аренды и расчет длительности
        rental.setEndTime(LocalDateTime.now());
        Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
        rental.setDuration(formatDuration(duration));

        // Сохранение обновленной аренды в базе данных
        Rental updatedRental = rentalRepository.save(rental);

        // Отправка сообщения в Kafka
        kafkaProducerService.sendMessage(updatedRental);

        // Возврат обновленного объекта аренды
        return updatedRental;
    }



    public String formatDuration(Duration duration) { //сделано public для теста, иначе ругается 0_0
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    @Override
    public Rental getRentalById(Long rentalId) {
        return rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException("Rental not found with id: " + rentalId));
    }

    @Override
    public List<Rental> getRentalsByUserId(Long userId) {
        return rentalRepository.findByUserId(userId);
    }
}
