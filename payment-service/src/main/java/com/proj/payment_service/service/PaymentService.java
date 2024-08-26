package com.proj.payment_service.service;

import com.proj.payment_service.dto.Rental;

public interface PaymentService {

    void processRental(Rental rental);
}
