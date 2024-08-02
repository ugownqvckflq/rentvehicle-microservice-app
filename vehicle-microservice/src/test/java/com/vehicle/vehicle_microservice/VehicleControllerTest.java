package com.vehicle.vehicle_microservice;

import com.vehicle.vehicle_microservice.controller.VehicleController;
import com.vehicle.vehicle_microservice.entity.*;
import com.vehicle.vehicle_microservice.services.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VehicleControllerTest {

    @Mock
    private VehicleService vehicleService;

    @InjectMocks
    private VehicleController vehicleController;

    private Vehicle car;
    private Vehicle scooter;

    @BeforeEach
    void setUp() {
        car = new Car();
        car.setId(1L);
        car.setModel("Tesla");
        car.setSpeed(150.0);
        car.setStatus(Status.AVAILABLE);
        car.setLicensePlate("TESLA123");

        scooter = new Scooter();
        scooter.setId(2L);
        scooter.setModel("Xiaomi");
        scooter.setSpeed(25.0);
        scooter.setStatus(Status.AVAILABLE);
        scooter.setLicensePlate("XIAOMI123");
    }

    @Test
    void testGetAllVehicles_AdminRole() {
        when(vehicleService.getAllVehicle()).thenReturn(Arrays.asList(car, scooter));

        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles("ROLE_ADMIN");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(vehicleService, times(1)).getAllVehicle();
    }

    @Test
    void testGetAllVehicles_Forbidden() {
        ResponseEntity<List<Vehicle>> response = vehicleController.getAllVehicles("ROLE_USER");

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(vehicleService, times(0)).getAllVehicle();
    }

    @Test
    void testGetVehicleById_Found() {
        when(vehicleService.getById(1L)).thenReturn(Optional.of(car));

        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
        verify(vehicleService, times(1)).getById(1L);
    }

    @Test
    void testGetVehicleById_NotFound() {
        when(vehicleService.getById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Vehicle> response = vehicleController.getVehicleById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(vehicleService, times(1)).getById(1L);
    }

    @Test
    void testCreateVehicle() {
        when(vehicleService.saveVehicle(car)).thenReturn(car);

        ResponseEntity<Vehicle> response = vehicleController.createVehicle(car);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
        verify(vehicleService, times(1)).saveVehicle(car);
    }

    @Test
    void testDeleteVehicle_AdminRole() {
        doNothing().when(vehicleService).deleteVehicle(1L);

        ResponseEntity<Void> response = vehicleController.deleteVehicle("ROLE_ADMIN", 1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(vehicleService, times(1)).deleteVehicle(1L);
    }

    @Test
    void testDeleteVehicle_Forbidden() {
        ResponseEntity<Void> response = vehicleController.deleteVehicle("ROLE_USER", 1L);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(vehicleService, times(0)).deleteVehicle(1L);
    }

    @Test
    void testUpdateVehicle_AdminRole() {
        Vehicle updatedCar = car;
        updatedCar.setModel("Tesla Model S");

        when(vehicleService.getById(1L)).thenReturn(Optional.of(car));
        when(vehicleService.saveVehicle(any(Vehicle.class))).thenReturn(updatedCar);

        ResponseEntity<Vehicle> response = vehicleController.updateVehicle("ROLE_ADMIN", 1L, updatedCar);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Tesla Model S", response.getBody().getModel());
        verify(vehicleService, times(1)).getById(1L);
        verify(vehicleService, times(1)).saveVehicle(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicle_Forbidden() {
        Vehicle updatedCar = car;
        updatedCar.setModel("Tesla Model S");

        ResponseEntity<Vehicle> response = vehicleController.updateVehicle("ROLE_USER", 1L, updatedCar);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        verify(vehicleService, times(0)).getById(1L);
        verify(vehicleService, times(0)).saveVehicle(any(Vehicle.class));
    }

    @Test
    void testGetByPlate_Found() {
        when(vehicleService.getByPlate("TESLA123")).thenReturn(Optional.of(car));

        ResponseEntity<Vehicle> response = vehicleController.getByPlate("TESLA123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
        verify(vehicleService, times(1)).getByPlate("TESLA123");
    }

    @Test
    void testGetByPlate_NotFound() {
        when(vehicleService.getByPlate("TESLA123")).thenReturn(Optional.empty());

        ResponseEntity<Vehicle> response = vehicleController.getByPlate("TESLA123");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(vehicleService, times(1)).getByPlate("TESLA123");
    }

    @Test
    void testGetAvailableVehicles() {
        when(vehicleService.getAvailableVehicles()).thenReturn(Arrays.asList(car, scooter));

        ResponseEntity<List<Vehicle>> response = vehicleController.getAvailableVehicles();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        verify(vehicleService, times(1)).getAvailableVehicles();
    }

    @Test
    void testSearchVehiclesByModel_Found() {
        when(vehicleService.searchVehiclesByModel("Tesla")).thenReturn(Arrays.asList(car));

        ResponseEntity<List<Vehicle>> response = vehicleController.searchVehiclesByModel("Tesla");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(vehicleService, times(1)).searchVehiclesByModel("Tesla");
    }

    @Test
    void testSearchVehiclesByModel_NotFound() {
        when(vehicleService.searchVehiclesByModel("Tesla")).thenReturn(Arrays.asList());

        ResponseEntity<List<Vehicle>> response = vehicleController.searchVehiclesByModel("Tesla");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(vehicleService, times(1)).searchVehiclesByModel("Tesla");
    }
}
