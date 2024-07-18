package com.project.rental_microservice.repository;

import com.project.rental_microservice.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    Optional<Rental> findByVehicleIdAndEndTimeIsNull(Long vehicleId);
}
