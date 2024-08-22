package com.proj.payment_service.kafka;

import com.proj.payment_service.dto.Rental;
import com.proj.payment_service.service.PaymentService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RentalConsumer {

    private final PaymentService paymentService;

    public RentalConsumer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @KafkaListener(topics = "rental_topic", groupId = "payment_group")
    public void listenRentalEvents(Rental rental) {
        paymentService.processRental(rental);
    }
}