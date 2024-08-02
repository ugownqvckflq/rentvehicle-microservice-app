package com.project.rental_microservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rental_microservice.controller.RentalController;
import com.project.rental_microservice.dto.RentalRequest;
import com.project.rental_microservice.dto.ReturnRequest;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.service.RentalService;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RentalController.class)
public class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RentalService rentalService;

    @MockBean
    private VehicleServiceClient vehicleServiceClient;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RentalController(rentalService, vehicleServiceClient)).build();
    }

    @Test
    public void testRentVehicle() throws Exception {
        RentalRequest rentalRequest = new RentalRequest();
        rentalRequest.setLicensePlate("ABC123");

        Rental rental = new Rental();
        rental.setId(1L);

        Mockito.when(rentalService.rentVehicle(any(), any(), any())).thenReturn(rental);

        mockMvc.perform(post("/rentals/rent")
                        .header("X-User-Id", "1")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rentalRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    public void testReturnVehicle() throws Exception {
        ReturnRequest returnRequest = new ReturnRequest();
        returnRequest.setLicensePlate("ABC123");

        Rental rental = new Rental();
        rental.setId(1L);

        Mockito.when(rentalService.returnVehicle(any(), any())).thenReturn(rental);

        mockMvc.perform(post("/rentals/return")
                        .header("Authorization", "Bearer testToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }
}
