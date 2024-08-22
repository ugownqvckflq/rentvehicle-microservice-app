package com.vehicle.vehicle_microservice;

import com.vehicle.vehicle_microservice.dto.VehicleCreateDTO;
import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;
import com.vehicle.vehicle_microservice.repository.VehicleRepository;
import com.vehicle.vehicle_microservice.services.VehicleServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class VehicleServiceImplTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private VehicleServiceImpl vehicleServiceImpl;

    public VehicleServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateVehicle_Car() {
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
        car.setFuelLevel(85.00);
        car.setHorsePower(661);
        car.setNumberOfDoors(2);
        car.setStatus(Status.AVAILABLE);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(car);

        Vehicle savedVehicle = vehicleServiceImpl.createVehicle(vehicleCreateDTO);
        assertNotNull(savedVehicle);
        assertEquals("FF-488", savedVehicle.getLicensePlate());
    }

    @Test
    void testCreateVehicle_DuplicateLicensePlate() {
        VehicleCreateDTO vehicleCreateDTO = new VehicleCreateDTO();
        vehicleCreateDTO.setLicensePlate("FF-488");
        vehicleCreateDTO.setVehicleType("CAR");

        when(vehicleRepository.save(any(Vehicle.class)))
                .thenThrow(new DataIntegrityViolationException("Duplicate license plate"));

        assertThrows(DuplicateLicensePlateException.class, () -> vehicleServiceImpl.createVehicle(vehicleCreateDTO));
    }

    @Test
    void testGetById() {
        Car car = new Car();
        car.setId(1L);
        car.setLicensePlate("FF-488");

        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(car));

        Optional<Vehicle> vehicle = vehicleServiceImpl.getById(1L);
        assertTrue(vehicle.isPresent());
        assertEquals("FF-488", vehicle.get().getLicensePlate());
    }

    @Test
    void testDeleteVehicle() {
        Long id = 1L;
        vehicleServiceImpl.deleteVehicle(id);
        verify(vehicleRepository, times(1)).deleteById(id);
    }
}