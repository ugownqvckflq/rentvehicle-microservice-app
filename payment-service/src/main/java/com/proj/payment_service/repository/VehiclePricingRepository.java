package com.proj.payment_service.repository;

import com.proj.payment_service.entity.VehiclePricing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiclePricingRepository extends JpaRepository<VehiclePricing, Long> {
    Optional<VehiclePricing> findByVehicleType(String vehicleType);
}
