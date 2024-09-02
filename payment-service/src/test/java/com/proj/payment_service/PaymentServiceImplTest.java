package com.proj.payment_service;

import com.proj.payment_service.entity.Card;
import com.proj.payment_service.entity.Payment;
import com.proj.payment_service.dto.Rental;
import com.proj.payment_service.entity.Vehicle;
import com.proj.payment_service.entity.VehiclePricing;
import com.proj.payment_service.exceptions.InsufficientBalanceException;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.repository.PaymentRepository;
import com.proj.payment_service.repository.VehiclePricingRepository;
import com.proj.payment_service.repository.VehicleRepository;
import com.proj.payment_service.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private VehiclePricingRepository vehiclePricingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testProcessRental_initialCost() {
        Rental rental = new Rental();
        rental.setUserId(1L);
        rental.setVehicleId(1L);
        rental.setStartTime(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType("CAR");

        VehiclePricing pricing = new VehiclePricing();
        pricing.setInitialCost(BigDecimal.valueOf(50.00));

        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));
        when(vehiclePricingRepository.findByVehicleType(anyString())).thenReturn(Optional.of(pricing));
        when(cardRepository.findByUserId(anyLong())).thenReturn(Optional.of(createCardWithBalance(BigDecimal.valueOf(100.00))));

        paymentService.processRental(rental);

        verify(cardRepository, times(1)).save(any(Card.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testProcessRental_usageCost() {
        Rental rental = new Rental();
        rental.setUserId(1L);
        rental.setVehicleId(1L);
        rental.setStartTime(LocalDateTime.now().minusHours(1));
        rental.setEndTime(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType("CAR");

        VehiclePricing pricing = new VehiclePricing();
        pricing.setPerMinuteCost(BigDecimal.valueOf(1.00));

        when(vehicleRepository.findById(anyLong())).thenReturn(Optional.of(vehicle));
        when(vehiclePricingRepository.findByVehicleType(anyString())).thenReturn(Optional.of(pricing));
        when(cardRepository.findByUserId(anyLong())).thenReturn(Optional.of(createCardWithBalance(BigDecimal.valueOf(100.00))));

        paymentService.processRental(rental);

        verify(cardRepository, times(1)).save(any(Card.class));
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }


    private Card createCardWithBalance(BigDecimal balance) {
        Card card = new Card();
        card.setBalance(balance);
        return card;
    }


}