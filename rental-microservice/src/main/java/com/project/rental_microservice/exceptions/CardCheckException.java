package com.project.rental_microservice.exceptions;

public class CardCheckException extends RuntimeException {
    public CardCheckException(String message) {
        super(message);
    }

    public CardCheckException(String message, Throwable cause) {
        super(message, cause);
    }
}
