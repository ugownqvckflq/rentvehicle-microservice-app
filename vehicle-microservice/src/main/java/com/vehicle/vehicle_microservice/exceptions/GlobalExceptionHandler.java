package com.vehicle.vehicle_microservice.exceptions;

import com.project.rolechecker.RoleCheckAspect;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RoleCheckAspect.UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorizedException(RoleCheckAspect.UnauthorizedException ex, WebRequest request) {
        return new ResponseEntity<>("Access Denied: " + ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DuplicateLicensePlateException.class)
    public ResponseEntity<String> handleDuplicateLicensePlateException(DuplicateLicensePlateException ex, WebRequest request) {
        return new ResponseEntity<>("Duplicate License Plate: " + ex.getMessage(), HttpStatus.CONFLICT);
    }


}
