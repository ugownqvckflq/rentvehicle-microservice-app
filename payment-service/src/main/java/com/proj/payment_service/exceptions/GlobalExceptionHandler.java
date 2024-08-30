package com.proj.payment_service.exceptions;

import com.proj.payment_service.dto.responses.ErrorResponse;
import com.proj.payment_service.exceptions.*;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolationException(ConstraintViolationException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "VALIDATION_ERROR", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "ILLEGAL_ARGUMENT", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CardAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyExistsException(CardAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "CARD_ALREADY_EXISTS", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFoundException(CardNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "CARD_NOT_FOUND", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalanceException(InsufficientBalanceException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "INSUFFICIENT_BALANCE", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleVehicleNotFoundException(VehicleNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "VEHICLE_NOT_FOUND", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(PricingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePricingNotFoundException(PricingNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "PRICING_NOT_FOUND", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse errorResponse = new ErrorResponse("Validation error: " + ex.getBindingResult(), "VALIDATION_ERROR", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse("An error occurred: " + ex.getMessage(), "INTERNAL_ERROR", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
