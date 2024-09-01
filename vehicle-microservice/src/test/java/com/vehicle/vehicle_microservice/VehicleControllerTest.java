package com.vehicle.vehicle_microservice;

import com.vehicle.vehicle_microservice.controller.VehicleController;
import com.vehicle.vehicle_microservice.dto.VehicleDto;
import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Scooter;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.services.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class VehicleControllerTest {

    @Mock
    private VehicleServiceImpl vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(vehicleController).build();
    }

    @Test
    void testGetAllVehicles() throws Exception {
        Car car = new Car();
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        Scooter scooter = new Scooter();
        scooter.setModel("Xiaomi M365");
        scooter.setSpeed(25.0);
        scooter.setStatus(Status.AVAILABLE);
        scooter.setLicensePlate("ES-2024");
        scooter.setBatteryLevel(75);

        List<Vehicle> vehicles = Arrays.asList(car, scooter);

        when(vehicleService.getAllVehicle()).thenReturn(vehicles);

        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].model").value("Ferrari 488"))
                .andExpect(jsonPath("$[1].model").value("Xiaomi M365"));

        verify(vehicleService, times(1)).getAllVehicle();
    }

    @Test
    void testGetVehicleById() throws Exception {
        Car car = new Car();
        car.setId(1L);
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        when(vehicleService.getById(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(get("/api/v1/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("Ferrari 488"));

        verify(vehicleService, times(1)).getById(1L);
    }

    @Test
    void testCreateVehicle() throws Exception {
        VehicleDto vehicleCreateDTO = new VehicleDto();
        vehicleCreateDTO.setModel("Ferrari 488");
        vehicleCreateDTO.setSpeed(211.0);
        vehicleCreateDTO.setStatus(Status.AVAILABLE);
        vehicleCreateDTO.setLicensePlate("FF-488");

        Car car = new Car();
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        when(vehicleService.createVehicle(vehicleCreateDTO)).thenReturn(car);

        mockMvc.perform(post("/api/v1/vehicles/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"model\":\"Ferrari 488\",\"speed\":211,\"status\":\"AVAILABLE\",\"licensePlate\":\"FF-488\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("Ferrari 488"));

        verify(vehicleService, times(1)).createVehicle(any(VehicleDto.class));
    }

    @Test
    void testDeleteVehicle() throws Exception {
        mockMvc.perform(delete("/api/v1/vehicles/1"))
                .andExpect(status().isNoContent());

        verify(vehicleService, times(1)).deleteVehicle(1L);
    }

    @Test
    void testGetByPlate() throws Exception {
        Scooter scooter = new Scooter();
        scooter.setId(1L);
        scooter.setModel("Xiaomi M365");
        scooter.setSpeed(25.0);
        scooter.setStatus(Status.AVAILABLE);
        scooter.setLicensePlate("ES-2024");
        scooter.setBatteryLevel(75);

        when(vehicleService.getByPlate("ES-2024")).thenReturn(Optional.of(scooter));

        mockMvc.perform(get("/api/v1/vehicles/license-plates/ES-2024"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("Xiaomi M365"));

        verify(vehicleService, times(1)).getByPlate("ES-2024");
    }

    @Test
    void testUpdateVehicle() throws Exception {
        VehicleDto vehicleCreateDTO = new VehicleDto();
        vehicleCreateDTO.setModel("Ferrari 488");
        vehicleCreateDTO.setSpeed(211.0);
        vehicleCreateDTO.setStatus(Status.AVAILABLE);
        vehicleCreateDTO.setLicensePlate("FF-488");

        Car existingCar = new Car();
        existingCar.setId(1L);
        existingCar.setModel("Ferrari 488");
        existingCar.setSpeed(211.0);
        existingCar.setStatus(Status.AVAILABLE);
        existingCar.setLicensePlate("FF-488");
        existingCar.setFuelLevel(85.0);
        existingCar.setHorsePower(661);
        existingCar.setNumberOfDoors(2);

        when(vehicleService.getById(1L)).thenReturn(Optional.of(existingCar));
        when(vehicleService.updateVehicle(existingCar, vehicleCreateDTO)).thenReturn(existingCar);

        mockMvc.perform(put("/api/v1/vehicles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"model\":\"Ferrari 488\",\"speed\":211,\"status\":\"AVAILABLE\",\"licensePlate\":\"FF-488\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.model").value("Ferrari 488"));

        verify(vehicleService, times(1)).getById(1L);
        verify(vehicleService, times(1)).updateVehicle(any(Vehicle.class), any(VehicleDto.class));
    }

    @Test
    void testGetAvailableVehicles() throws Exception {
        Car car = new Car();
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        when(vehicleService.getAvailableVehicles()).thenReturn(Arrays.asList(car));

        mockMvc.perform(get("/api/v1/vehicles/available"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].model").value("Ferrari 488"));

        verify(vehicleService, times(1)).getAvailableVehicles();
    }

    @Test
    void testSetVehicleStatusById() throws Exception {
        Car car = new Car();
        car.setId(1L);
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        when(vehicleService.getById(1L)).thenReturn(Optional.of(car));

        mockMvc.perform(post("/api/v1/vehicles/1/status/UNAVAILABLE"))
                .andExpect(status().isOk());

        verify(vehicleService, times(1)).getById(1L);
        verify(vehicleService, times(1)).saveVehicle(any(Vehicle.class));
    }

    @Test
    void testSearchVehiclesByModel() throws Exception {
        Car car = new Car();
        car.setModel("Ferrari 488");
        car.setSpeed(211.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("FF-488");
        car.setFuelLevel(85.0);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);

        when(vehicleService.searchVehiclesByModel("Ferrari 488")).thenReturn(Arrays.asList(car));

        mockMvc.perform(get("/api/v1/vehicles/search/Ferrari 488"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].model").value("Ferrari 488"));

        verify(vehicleService, times(1)).searchVehiclesByModel("Ferrari 488");
    }

}
