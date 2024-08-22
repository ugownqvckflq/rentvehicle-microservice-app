package com.project.rental_microservice;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.exceptions.RentalNotFoundException;
import com.project.rental_microservice.repository.RentalRepository;
import com.project.rental_microservice.service.KafkaProducerService;
import com.project.rental_microservice.service.RentalServiceImpl;
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

    @InjectMocks
    private RentalServiceImpl rentalService;

    @Mock
    private VehicleServiceClient vehicleServiceClient;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void rentVehicle_ShouldCreateAndSaveRental() {
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("XYZ123");
        String jwtToken = "dummyToken";
        Long userId = 1L;

        VehicleDto vehicle = new VehicleDto();
        vehicle.setId(1L);
        vehicle.setLicensePlate("XYZ123");

        Rental rental = new Rental();
        rental.setUserId(userId);
        rental.setVehicleId(vehicle.getId());
        rental.setStartTime(LocalDateTime.now());

        when(vehicleServiceClient.getVehicleByLicensePlate(anyString(), anyString())).thenReturn(Mono.just(vehicle));
        when(rentalRepository.save(any(Rental.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Rental result = rentalService.rentVehicle(userId, rentalRequest, jwtToken);

        ArgumentCaptor<Rental> rentalCaptor = ArgumentCaptor.forClass(Rental.class);
        verify(rentalRepository).save(rentalCaptor.capture());

        Rental capturedRental = rentalCaptor.getValue();
        assertNotNull(capturedRental);
        assertEquals(userId, capturedRental.getUserId());
        assertEquals(vehicle.getId(), capturedRental.getVehicleId());
        assertNotNull(capturedRental.getStartTime());
    }


    @Test
    void returnVehicle_ShouldUpdateAndSaveRental() {
        String jwtToken = "some-jwt-token";
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLicensePlate("XYZ123");

        VehicleDto vehicleDto = new VehicleDto();
        vehicleDto.setId(1L);

        Rental rental = new Rental();
        rental.setVehicleId(vehicleDto.getId());
        rental.setStartTime(LocalDateTime.now().minusHours(1));
        rental.setEndTime(null);

        when(vehicleServiceClient.getVehicleByLicensePlate(anyString(), anyString()))
                .thenReturn(Mono.just(vehicleDto));
        doNothing().when(vehicleServiceClient).setVehicleStatus(anyLong(), eq("AVAILABLE"), anyString());
        when(rentalRepository.findByVehicleIdAndEndTimeIsNull(anyLong()))
                .thenReturn(Optional.of(rental));
        when(rentalRepository.save(any(Rental.class))).thenReturn(rental);

        Rental result = rentalService.returnVehicle(returnRequest, jwtToken);

        assertNotNull(result);
        assertNotNull(result.getEndTime());
        Duration duration = Duration.between(rental.getStartTime(), result.getEndTime());
        String formattedDuration = rentalService.formatDuration(duration);
        assertEquals(formattedDuration, result.getDuration());
        verify(vehicleServiceClient).setVehicleStatus(vehicleDto.getId(), "AVAILABLE", jwtToken);
        verify(rentalRepository).save(result);
        verify(kafkaProducerService).sendMessage(result);
    }

    @Test
    void getRentalById_ShouldReturnRental() {
        Long rentalId = 1L;
        Rental rental = new Rental();
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.of(rental));

        Rental result = rentalService.getRentalById(rentalId);

        assertNotNull(result);
        assertEquals(rental, result);
    }

    @Test
    void getRentalById_ShouldThrowException_WhenRentalNotFound() {
        Long rentalId = 1L;
        when(rentalRepository.findById(rentalId)).thenReturn(Optional.empty());

        assertThrows(RentalNotFoundException.class, () -> rentalService.getRentalById(rentalId));
    }

    @Test
    void getRentalsByUserId_ShouldReturnRentals() {
        Long userId = 1L;
        List<Rental> rentals = List.of(new Rental(), new Rental());
        when(rentalRepository.findByUserId(userId)).thenReturn(rentals);

        List<Rental> result = rentalService.getRentalsByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }
}
