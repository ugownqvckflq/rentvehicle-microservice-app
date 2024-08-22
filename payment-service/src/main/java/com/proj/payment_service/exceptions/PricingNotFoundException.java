package com.proj.payment_service.exceptions;

public class PricingNotFoundException extends RuntimeException {
    public PricingNotFoundException(String message) {
        super(message);
    }
}