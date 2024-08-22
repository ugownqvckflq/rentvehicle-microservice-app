package com.vehicle.vehicle_microservice;

import com.vehicle.vehicle_microservice.controller.VehicleController;
import com.vehicle.vehicle_microservice.dto.VehicleCreateDTO;
import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.services.VehicleServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VehicleControllerTest {

    @Mock
    private VehicleServiceImpl vehicleServiceImpl;

    @InjectMocks
    private VehicleController vehicleController;

    private final MockMvc mockMvc;

    public VehicleControllerTest() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
    }

    @Test
    void testGetVehicleById() throws Exception {
        Car car = new Car();
        car.setId(1L);
        car.setLicensePlate("FF-488");

        when(vehicleServiceImpl.getById(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("FF-488"));
    }

    @Test
    void testCreateVehicle() throws Exception {
        VehicleCreateDTO vehicleCreateDTO = new VehicleCreateDTO();
        vehicleCreateDTO.setModel("Ferrari 488");
        vehicleCreateDTO.setSpeed(211.00);
        vehicleCreateDTO.setLicensePlate("FF-488");
        vehicleCreateDTO.setVehicleType("CAR");
        vehicleCreateDTO.setFuelLevel(85.00);
        vehicleCreateDTO.setHorsePower(661);
        vehicleCreateDTO.setNumberOfDoors(2);
        vehicleCreateDTO.setStatus(Status.AVAILABLE);

        Car car = new Car();
        car.setModel("Ferrari 488");
        car.setSpeed(211.00);
        car.setLicensePlate("FF-488");

        when(vehicleServiceImpl.createVehicle(any(VehicleCreateDTO.class))).thenReturn(car);

        mockMvc.perform(post("/vehicles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"model\":\"Ferrari 488\",\"speed\":211.00,\"licensePlate\":\"FF-488\",\"vehicleType\":\"CAR\",\"fuelLevel\":85.00,\"horsePower\":661,\"numberOfDoors\":2,\"status\":\"AVAILABLE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.licensePlate").value("FF-488"));
    }
}
