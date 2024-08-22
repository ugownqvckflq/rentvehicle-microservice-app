package com.project.rental_microservice.exceptions;

public class InvalidUserIdFormatException extends RuntimeException {
    public InvalidUserIdFormatException(String message) {
        super(message);
    }
}
