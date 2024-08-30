package com.vehicle.vehicle_microservice.controller;


import com.project.rolechecker.RoleCheck;
import com.vehicle.vehicle_microservice.dto.VehicleCreateDTO;
import com.vehicle.vehicle_microservice.entity.Status;
import com.vehicle.vehicle_microservice.entity.Vehicle;
import com.vehicle.vehicle_microservice.exceptions.DuplicateLicensePlateException;
import com.vehicle.vehicle_microservice.services.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


    @Operation(
            summary = "Получить все транспортные средства (только для админа)",
            description = "Получить список всех транспортных средств",
            parameters = {
                    @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Роль пользователя", required = false, example = "ROLE_ADMIN")
            }
    )
    @RoleCheck("ROLE_ADMIN")
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicle();
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Получить транспортное средство по ID",
            description = "Получить транспортное средство по его ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        Optional<Vehicle> vehicle = vehicleService.getById(id);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Создать новое транспортное средство",
            description = "Создать новое транспортное средство с указанными данными"
    )
    @PostMapping("/create")
    public ResponseEntity<Vehicle> createVehicle(@RequestBody VehicleCreateDTO vehicleCreateDTO) {
        try {
            Vehicle savedVehicle = vehicleService.createVehicle(vehicleCreateDTO);
            return ResponseEntity.ok(savedVehicle);
        } catch (DuplicateLicensePlateException e) {
            // Обработка исключения передается глобальному обработчику
            throw e;
        }
    }

    @Operation(
            summary = "Удалить транспортное средство по ID (только для админа)",
            description = "Удалить транспортное средство по его ID",
            parameters = {
                    @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Роль пользователя", required = false, example = "ROLE_ADMIN")
            }
    )
    @RoleCheck("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Получить транспортное средство по номерному знаку",
            description = "Получить транспортное средство по номерному знаку"
    )
    @GetMapping("/license-plates/{licensePlate}")
    public ResponseEntity<Vehicle> getByPlate(@PathVariable String licensePlate) {
        Optional<Vehicle> vehicle = vehicleService.getByPlate(licensePlate);
        return vehicle.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Обновить транспортное средство по ID (только для админа)",
            description = "Обновить детали транспортного средства по его ID",
            parameters = {
                    @Parameter(name = "X-User-Role", in = ParameterIn.HEADER, description = "Роль пользователя", required = false, example = "ROLE_ADMIN")
            }
    )
    @RoleCheck("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> updateVehicle(@PathVariable Long id, @RequestBody VehicleCreateDTO vehicleCreateDTO) {
        Optional<Vehicle> vehicleOptional = vehicleService.getById(id);
        if (vehicleOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Vehicle existingVehicle = vehicleOptional.get();
        Vehicle updatedVehicle = vehicleService.updateVehicle(existingVehicle, vehicleCreateDTO);
        return ResponseEntity.ok(updatedVehicle);
    }

    @Operation(
            summary = "Получить доступные транспортные средства",
            description = "Получить список всех доступных транспортных средств"
    )
    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> getAvailableVehicles() {
        List<Vehicle> availableVehicles = vehicleService.getAvailableVehicles();
        return ResponseEntity.ok(availableVehicles);
    }

    @Operation(
            summary = "Установить статус транспортного средства по ID",
            description = "Установить статус транспортного средства по его ID"
    )
    @PostMapping("/{id}/status/{status}")
    public ResponseEntity<Vehicle> setVehicleStatusById(@PathVariable Long id, @PathVariable Status status) {
        Vehicle vehicle = vehicleService.getById(id).orElseThrow();
        vehicle.setStatus(status);
        vehicleService.saveVehicle(vehicle);
        return ResponseEntity.ok(vehicle);
    }

    @Operation(
            summary = "Поиск транспортных средств по модели",
            description = "Поиск транспортных средств по модели"
    )
    @GetMapping("/search/{model}")
    public ResponseEntity<List<Vehicle>> searchVehiclesByModel(@PathVariable String model) {
        List<Vehicle> vehicles = vehicleService.searchVehiclesByModel(model);
        if (vehicles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicles);
    }

    @Operation(
            summary = "Фильтрация транспортных средств",
            description = "Фильтрует транспортные средства по указанным критериям"
    )
    @GetMapping("/filter")
    public ResponseEntity<List<Vehicle>> filterVehicles(
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Double minFuelLevel,
            @RequestParam(required = false) Double maxFuelLevel,
            @RequestParam(required = false) Integer minBatteryLevel,
            @RequestParam(required = false) Integer maxBatteryLevel,
            @RequestParam(required = false) Status status
    ) {
        List<Vehicle> vehicles = vehicleService.filterVehicles(model, minFuelLevel, maxFuelLevel, minBatteryLevel, maxBatteryLevel, status);
        if (vehicles.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(vehicles);
    }

}
