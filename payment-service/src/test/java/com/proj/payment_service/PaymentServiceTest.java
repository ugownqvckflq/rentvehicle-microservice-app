package com.proj.payment_service;

import com.proj.payment_service.dto.Rental;
import com.proj.payment_service.entity.Card;
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

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

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
    void testProcessRental_InsufficientBalance() {
        Rental rental = new Rental();
        rental.setUserId(1L);
        rental.setVehicleId(1L);
        rental.setStartTime(LocalDateTime.now());

        Vehicle vehicle = new Vehicle();
        vehicle.setVehicleType("CAR");

        VehiclePricing pricing = new VehiclePricing();
        pricing.setInitialCost(BigDecimal.TEN);

        Card card = new Card();
        card.setBalance(BigDecimal.ZERO);

        when(vehicleRepository.findById(rental.getVehicleId())).thenReturn(Optional.of(vehicle));
        when(vehiclePricingRepository.findByVehicleType(vehicle.getVehicleType())).thenReturn(Optional.of(pricing));
        when(cardRepository.findByUserId(rental.getUserId())).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> paymentService.processRental(rental))
                .isInstanceOf(InsufficientBalanceException.class)
                .hasMessage("Insufficient balance");

        verify(cardRepository, never()).save(any(Card.class));
        verify(paymentRepository, never()).save(any());
    }


}
