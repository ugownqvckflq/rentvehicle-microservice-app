package com.project.rental_microservice.exceptions.handler;

import com.project.rental_microservice.dto.responses.ErrorResponse;
import com.project.rental_microservice.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "VEHICLE_NOT_FOUND", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(RentalNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRentalNotFoundException(RentalNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "RENTAL_NOT_FOUND", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InvalidUserIdFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidUserIdFormatException(InvalidUserIdFormatException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INVALID_USER_ID", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(VehicleStatusUpdateException.class)
    public ResponseEntity<String> handleVehicleStatusUpdateException(VehicleStatusUpdateException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(ExternalServiceException.class)
    public ResponseEntity<String> handleExternalServiceException(ExternalServiceException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("External service error: " + ex.getMessage());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ErrorResponse> handleWebClientResponseException(WebClientResponseException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getResponseBodyAsString(), "WEB_CLIENT_ERROR", LocalDateTime.now());
        return ResponseEntity.status(ex.getRawStatusCode()).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("Error occurred: " + ex.getMessage(), "INTERNAL_ERROR", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
