package com.project.rental_microservice;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.service.KafkaProducerService;
import com.project.rental_microservice.service.RentalServiceImpl;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRentVehicle() {
        // Given
        String userId = "1";
        String jwtToken = "testToken";
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("ABC123");

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(1L);

        when(vehicleServiceClient.getVehicleByLicensePlate(anyString(), anyString())).thenReturn(Mono.just(vehicleDto));
        when(rentalRepository.save(any(Rental.class))).thenReturn(new Rental());

        // When
        Rental rental = rentalService.rentVehicle(userId, rentalRequest, jwtToken);

        // Then
        assertNotNull(rental);
        verify(vehicleServiceClient, times(1)).setVehicleStatus(vehicleDto.getId(), "UNAVAILABLE", jwtToken);
        verify(kafkaProducerService, times(1)).sendMessage(any(Rental.class));
    }

    @Test
    public void testReturnVehicle() {
        // Given
        String jwtToken = "testToken";
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLicensePlate("ABC123");

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(1L);

        Rental rental = new Rental();
        rental.setId(1L);
        rental.setStartTime(LocalDateTime.now().minusHours(2));

        when(vehicleServiceClient.getVehicleByLicensePlate(anyString(), anyString())).thenReturn(Mono.just(vehicleDto));
        when(rentalRepository.findByVehicleIdAndEndTimeIsNull(vehicleDto.getId())).thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        // When
        Rental returnedRental = rentalService.returnVehicle(returnRequest, jwtToken);

        // Then
        assertNotNull(returnedRental);
        assertNotNull(returnedRental.getEndTime());
        verify(vehicleServiceClient, times(1)).setVehicleStatus(vehicleDto.getId(), "AVAILABLE", jwtToken);
        verify(kafkaProducerService, times(1)).sendMessage(any(Rental.class));
    }
}
