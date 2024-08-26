package com.vehicle.vehicle_microservice.repository;

import com.vehicle.vehicle_microservice.entity.Car;
import com.vehicle.vehicle_microservice.entity.Scooter;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlate(String plate);

    List<Vehicle> findByStatus(Status status);
    List<Vehicle> findByModel(String model);


    @Query("SELECT v FROM Car v WHERE v.fuelLevel >= :minFuelLevel AND v.fuelLevel <= :maxFuelLevel")
    List<Vehicle> findAvailableCarsByFuelLevelBetween(@Param("minFuelLevel") double minFuelLevel, @Param("maxFuelLevel") double maxFuelLevel);

    @Query("SELECT v FROM Scooter v WHERE v.batteryLevel >= :minBatteryLevel AND v.batteryLevel <= :maxBatteryLevel")
    List<Vehicle> findAvailableScootersByBatteryLevelBetween(@Param("minBatteryLevel") int minBatteryLevel, @Param("maxBatteryLevel") int maxBatteryLevel);


}
