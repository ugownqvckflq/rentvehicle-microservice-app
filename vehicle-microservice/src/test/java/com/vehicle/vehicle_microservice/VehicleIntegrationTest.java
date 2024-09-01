package com.vehicle.vehicle_microservice;

import com.vehicle.vehicle_microservice.dto.VehicleDto;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VehicleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private VehicleRepository vehicleRepository;


    @Test
    void testGetAllVehicles() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Role", "ROLE_ADMIN");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Vehicle[]> response = restTemplate.exchange(
                "/api/v1/vehicles",
                HttpMethod.GET,
                entity,
                Vehicle[].class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotEmpty();
    }

    @Test
    void testCreateVehicle() {
        VehicleDto vehicleCreateDTO = new VehicleDto();
        vehicleCreateDTO.setModel("Ferrari 488");
        vehicleCreateDTO.setSpeed(211.00);
        vehicleCreateDTO.setLicensePlate("FF-440");
        vehicleCreateDTO.setVehicleType("CAR");
        vehicleCreateDTO.setFuelLevel(85.00);
        vehicleCreateDTO.setHorsePower(661);
        vehicleCreateDTO.setNumberOfDoors(2);
        vehicleCreateDTO.setStatus(Status.AVAILABLE);

        ResponseEntity<Vehicle> response = restTemplate.postForEntity("/api/v1/vehicles/create", vehicleCreateDTO, Vehicle.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getLicensePlate()).isEqualTo("FF-440");

        Optional<Vehicle> vehicles = vehicleRepository.findByLicensePlate("FF-440");
        assertThat(vehicles).isNotEmpty();
    }


}
