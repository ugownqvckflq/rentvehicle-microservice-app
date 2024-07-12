package com.vehicle.vehicle_microservice.exceptions;

public class DuplicateLicensePlateException extends RuntimeException{
    public DuplicateLicensePlateException(String message) {
        super(message);
    }
}
