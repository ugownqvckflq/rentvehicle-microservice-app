package com.project.rental_microservice.exceptions;


public class VehicleStatusUpdateException extends RuntimeException {
    public VehicleStatusUpdateException(String message) {
        super(message);
    }
}
