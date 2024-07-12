package com.project.rental_microservice.restTemplate.service;

import com.project.rental_microservice.dto.VehicleDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class VehicleServiceClient {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${vehicle.service.url}")
    private String vehicleServiceUrl;

    public VehicleDto getVehicleByLicensePlate(String licensePlate) {
        try {
            return restTemplate.getForObject(vehicleServiceUrl + licensePlate, VehicleDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }
}
