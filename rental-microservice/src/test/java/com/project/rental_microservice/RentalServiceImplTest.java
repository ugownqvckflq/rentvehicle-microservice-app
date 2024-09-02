package com.project.rental_microservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.exceptions.RentalNotFoundException;
import com.project.rental_microservice.exceptions.VehicleUnavailableException;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.config.kafka.KafkaProducerService;
import com.project.rental_microservice.service.RentalServiceImpl;
import com.project.rental_microservice.service.jwt.JwtUtils;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class RentalServiceImplTest {

    @Mock
    private VehicleServiceClient vehicleServiceClient;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @InjectMocks
    private RentalServiceImpl rentalService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRentalById_Found() {
        Long rentalId = 10L;
        Rental rental = new Rental();
        rental.setId(rentalId);
        rental.setUserId(1L);
        rental.setVehicleId(100L);

        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        Rental result = rentalService.getRentalById(rentalId);

        assertNotNull(result);
        assertEquals(rentalId, result.getId());

        verify(rentalRepository, times(1)).findById(rentalId);
    }

    @Test
    void rentVehicle_Success() {
        Long userId = 1L;
        String jwtToken = "valid.jwt.token";
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("FF-488");

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(100L);
        vehicleDto.setStatus("AVAILABLE");

        when(vehicleServiceClient.getVehicleByLicensePlate("FF-488", jwtToken))
                .thenReturn(Mono.just(vehicleDto));

        Rental rental = new Rental();
        rental.setId(10L);
        rental.setUserId(userId);
        rental.setVehicleId(vehicleDto.getId());
        rental.setStartTime(LocalDateTime.now());

        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        Rental result = rentalService.rentVehicle(userId, rentalRequest, jwtToken);

        assertNotNull(result);
        assertEquals(userId, result.getUserId());
        assertEquals(vehicleDto.getId(), result.getVehicleId());
        assertNotNull(result.getStartTime());

        verify(vehicleServiceClient, times(1)).setVehicleStatus(vehicleDto.getId(), "UNAVAILABLE", jwtToken);
        verify(rentalRepository, times(1)).save(any(Rental.class));
        verify(kafkaProducerService, times(1)).sendMessage(rental);
    }

    @Test
    void rentVehicle_VehicleUnavailable() {
        Long userId = 1L;
        String jwtToken = "valid.jwt.token";
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("FF-488");

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(100L);
        vehicleDto.setStatus("UNAVAILABLE");

        when(vehicleServiceClient.getVehicleByLicensePlate("FF-488", jwtToken))
                .thenReturn(Mono.just(vehicleDto));

        VehicleUnavailableException exception = assertThrows(VehicleUnavailableException.class, () -> {
            rentalService.rentVehicle(userId, rentalRequest, jwtToken);
        });

        assertEquals("Vehicle with license plate FF-488 is currently unavailable for rent.", exception.getMessage());

        verify(vehicleServiceClient, never()).setVehicleStatus(anyLong(), anyString(), anyString());
        verify(rentalRepository, never()).save(any(Rental.class));
        verify(kafkaProducerService, never()).sendMessage(any(Rental.class));
    }

}
