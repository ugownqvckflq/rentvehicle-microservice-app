package com.project.rental_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rental_microservice.controller.RentalController;
import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.exceptions.InvalidUserIdFormatException;
import com.project.rental_microservice.exceptions.RentalNotFoundException;
import com.project.rental_microservice.exceptions.VehicleNotFoundException;
import com.project.rental_microservice.service.RentalService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.NestedServletException;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class RentalControllerTest {

    @Mock
    private RentalService rentalService;

    @InjectMocks
    private RentalController rentalController;

    private MockMvc mockMvc;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(rentalController).build();
    }

    @Test
    void rentVehicle_Success() throws Exception {
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("FF-488");

        Rental rental = new Rental();
        rental.setId(10L);
        rental.setUserId(1L);
        rental.setVehicleId(100L);
        rental.setStartTime(LocalDateTime.now());

        when(rentalService.rentVehicle(eq(1L), any(RentalRequest.class), eq("valid.jwt.token")))
                .thenReturn(rental);

        mockMvc.perform(post("/api/v1/rentals/rent")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token")
                        .header("X-User-Id", "1")
                        .contentType("application/json")
                        .content("{ \"licensePlate\": \"FF-488\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.vehicleId").value(100L));

        verify(rentalService, times(1)).rentVehicle(eq(1L), any(RentalRequest.class), eq("valid.jwt.token"));
    }

    @Test
    void returnVehicle_Success() throws Exception {
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLicensePlate("FF-488");

        Rental rental = new Rental();
        rental.setId(10L);
        rental.setUserId(1L);
        rental.setVehicleId(100L);
        rental.setStartTime(LocalDateTime.now());

        when(rentalService.returnVehicle(any(ReturnRequest.class), eq("valid.jwt.token")))
                .thenReturn(rental);

        mockMvc.perform(post("/api/v1/rentals/return")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer valid.jwt.token")
                        .contentType("application/json")
                        .content("{ \"licensePlate\": \"FF-488\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.vehicleId").value(100L));

        verify(rentalService, times(1)).returnVehicle(any(ReturnRequest.class), eq("valid.jwt.token"));
    }

    @Test
    void getRentalById_Success() throws Exception {
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setUserId(1L);
        rental.setVehicleId(100L);
        rental.setStartTime(LocalDateTime.now());

        when(rentalService.getRentalById(10L)).thenReturn(rental);

        mockMvc.perform(get("/api/v1/rentals/10")
                        .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.userId").value(1L))
                .andExpect(jsonPath("$.vehicleId").value(100L));

        verify(rentalService, times(1)).getRentalById(10L);
    }

    @Test
    void getRentalsByUserId_Success() throws Exception {
        Rental rental = new Rental();
        rental.setId(10L);
        rental.setUserId(1L);
        rental.setVehicleId(100L);
        rental.setStartTime(LocalDateTime.now());

        when(rentalService.getRentalsByUserId(1L)).thenReturn(Collections.singletonList(rental));

        mockMvc.perform(get("/api/v1/rentals/users/1/rentals")
                        .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L))
                .andExpect(jsonPath("$[0].userId").value(1L))
                .andExpect(jsonPath("$[0].vehicleId").value(100L));

        verify(rentalService, times(1)).getRentalsByUserId(1L);
    }

}
