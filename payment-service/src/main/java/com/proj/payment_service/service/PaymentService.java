package com.proj.payment_service.service;


import com.proj.payment_service.dto.Rental;
import com.proj.payment_service.entity.Payment;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final CardRepository cardRepository;

    private final PaymentRepository paymentRepository;

    public PaymentService(CardRepository cardRepository, PaymentRepository paymentRepository) {
        this.cardRepository = cardRepository;
        this.paymentRepository = paymentRepository;
    }

    public void processRental(Rental rental) {
        Payment payment = new Payment();
        payment.setRentalId(rental.getUserId());
        payment.setVehicleId(rental.getVehicleId());
        payment.setUserId(rental.getUserId());
        payment.setAmount(calculateAmount(rental));
        payment.setPaymentDate(LocalDateTime.now());
        payment.setRentalStartTime(String.valueOf(rental.getStartTime()));
        payment.setRentalEndTime(String.valueOf(rental.getEndTime()));

        paymentRepository.save(payment);
    }

    private double calculateAmount(Rental rental) {
        // Логика расчета суммы аренды
        return 100.0; // временная заглушка
    }

    private Payment createPayment(Rental rental, Double amount) {
        Payment payment = new Payment();
        payment.setUserId(rental.getUserId());
        payment.setVehicleId(rental.getVehicleId());
        payment.setAmount(amount);
        payment.setRentalStartTime(String.valueOf(rental.getStartTime()));
        payment.setRentalEndTime(String.valueOf(rental.getEndTime()));
        return payment;
    }
}