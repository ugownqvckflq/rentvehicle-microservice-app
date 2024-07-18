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

    private final RestTemplate restTemplate; //TODO переделать на WebClient либо на Openfeign

    @Value("${vehicle.service.url}")
    private String vehicleServiceUrl;

    public VehicleServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public VehicleDto getVehicleByLicensePlate(String licensePlate) {
        try {
            return restTemplate.getForObject(vehicleServiceUrl + "/plate/" + licensePlate, VehicleDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            } else {
                throw e;
            }
        }
    }

    public void setVehicleStatus(Long id, String status) { //TODO помогите...
        try {
             restTemplate.postForObject(vehicleServiceUrl + "/set-status/" + id + "/" + status, null, VehicleDto.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
            } else {
                throw e;
            }
        }
    }
}
