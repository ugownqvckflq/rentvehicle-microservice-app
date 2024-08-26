package com.project.rental_microservice.exceptions;

public class ExternalServiceException extends RuntimeException {
    private final String detailedMessage;

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
        this.detailedMessage = cause != null ? cause.toString() : "";
    }

    public ExternalServiceException(String message) {
        super(message);
        this.detailedMessage = "";
    }

    public String getDetailedMessage() {
        return detailedMessage;
    }
}
