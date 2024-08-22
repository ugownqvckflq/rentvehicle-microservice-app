package com.proj.payment_service.service;


import com.proj.payment_service.dto.Rental;
import com.proj.payment_service.entity.Vehicle;
import com.proj.payment_service.entity.Card;
import com.proj.payment_service.entity.Payment;
import com.proj.payment_service.entity.VehiclePricing;
import com.proj.payment_service.exceptions.CardNotFoundException;
import com.proj.payment_service.exceptions.InsufficientBalanceException;
import com.proj.payment_service.exceptions.PricingNotFoundException;
import com.proj.payment_service.exceptions.VehicleNotFoundException;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.repository.PaymentRepository;
import com.proj.payment_service.repository.VehiclePricingRepository;
import com.proj.payment_service.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final CardRepository cardRepository;
    private final VehiclePricingRepository vehiclePricingRepository;
    private final PaymentRepository paymentRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public void processRental(Rental rental) {
        // Найти транспорт по ID
        Vehicle vehicle = vehicleRepository.findById(rental.getVehicleId())
                .orElseThrow(() ->  new VehicleNotFoundException("Vehicle not found for ID: " + rental.getVehicleId()));

        // Получить тип транспорта и найти соответствующий тариф
        VehiclePricing pricing = vehiclePricingRepository.findByVehicleType(vehicle.getVehicleType())
                .orElseThrow(() -> new PricingNotFoundException("Pricing not found for vehicle type: " + vehicle.getVehicleType()));

        if (rental.getEndTime() == null) {
            // Списать начальную сумму при начале аренды
            chargeInitialCost(rental, pricing);
        } else {
            // Списать сумму за время использования при возврате
            Duration duration = Duration.between(rental.getStartTime(), rental.getEndTime());
            chargeUsageCost(rental, duration, pricing);
        }
    }

    private void chargeInitialCost(Rental rental, VehiclePricing pricing) {
        Card card = getCardByUserId(rental.getUserId());
        BigDecimal amount = pricing.getInitialCost();
        deductFunds(card, amount);
        savePayment(rental.getUserId(), amount, "Initial cost for rental");
    }

    private void chargeUsageCost(Rental rental, Duration duration, VehiclePricing pricing) {
        long minutes = duration.toMinutes() + (duration.getSeconds() % 60 == 0 ? 0 : 1); // округление до большего
        BigDecimal usageCost = pricing.getPerMinuteCost().multiply(BigDecimal.valueOf(minutes));
        Card card = getCardByUserId(rental.getUserId());
        deductFunds(card, usageCost);
        savePayment(rental.getUserId(), usageCost, "Usage cost for rental");
    }

    private Card getCardByUserId(Long userId) {
        return cardRepository.findByUserId(userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found for user: " + userId));
    }

    private void deductFunds(Card card, BigDecimal amount) {
        BigDecimal newBalance = card.getBalance().subtract(amount);
        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientBalanceException("Insufficient balance");
        }
        card.setBalance(newBalance);
        cardRepository.save(card);
    }

    private void savePayment(Long userId, BigDecimal amount, String description) {
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setDescription(description);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }
}