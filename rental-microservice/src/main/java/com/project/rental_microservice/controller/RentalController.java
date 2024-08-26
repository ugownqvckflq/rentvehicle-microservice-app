package com.project.rental_microservice.controller;

import com.project.rental_microservice.dto.requests.RentalRequest;
import com.project.rental_microservice.dto.requests.ReturnRequest;
import com.project.rental_microservice.entity.Rental;
import com.project.rental_microservice.exceptions.InvalidUserIdFormatException;
import com.project.rental_microservice.service.RentalService;
import com.project.rolechecker.RoleCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;

    @Operation(
            summary = "Аренда транспортного средства",
            description = "Позволяет пользователю арендовать транспортное средство. Необходимо предоставить JWT-токен и идентификатор пользователя в заголовках запроса.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя, выполняющего аренду", required = true, in = ParameterIn.HEADER),
                    @Parameter(name = HttpHeaders.AUTHORIZATION, description = "JWT-токен для аутентификации пользователя", required = true, in = ParameterIn.HEADER)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для аренды транспортного средства",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RentalRequest.class)
                    )
            )
    )
    @PostMapping("/rent")
    public ResponseEntity<Rental> rentVehicle(HttpServletRequest request, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody RentalRequest rentalRequest) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");
        Long userId = RentalService.extractUserIdFromHeader(request);

        return ResponseEntity.ok(rentalService.rentVehicle(userId, rentalRequest, jwtToken));
    }

    @Operation(
            summary = "Возврат транспортного средства",
            description = "Позволяет пользователю вернуть транспортное средство. Необходимо предоставить JWT-токен в заголовке запроса.",
            parameters = {
                    @Parameter(name = HttpHeaders.AUTHORIZATION, description = "JWT-токен для аутентификации пользователя", required = true, in = ParameterIn.HEADER)
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для возврата транспортного средства",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ReturnRequest.class)
                    )
            )
    )
    @PostMapping("/return")
    public ResponseEntity<Rental> returnVehicle(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader, @RequestBody ReturnRequest returnRequest) {
        String jwtToken = authorizationHeader.replace("Bearer ", "");

        return ResponseEntity.ok(rentalService.returnVehicle(returnRequest, jwtToken));
    }

    @Operation(
            summary = "Получить аренду по ID (только для админа)",
            description = "Получить информацию об аренде по идентификатору. Доступно только для пользователей с ролью ROLE_ADMIN.",
            parameters = {
                    @Parameter(name = "rentalId", description = "Идентификатор аренды", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            }
    )
    @RoleCheck("ROLE_ADMIN")
    @GetMapping("/{rentalId}")
    public ResponseEntity<Rental> getRentalById(@PathVariable Long rentalId) {
        return ResponseEntity.ok(rentalService.getRentalById(rentalId));
    }

    @Operation(
            summary = "Получить аренды по ID пользователя (только для админа)",
            description = "Получить список всех аренды для конкретного пользователя по его ID. Доступно только для пользователей с ролью ROLE_ADMIN.",
            parameters = {
                    @Parameter(name = "userId", description = "Идентификатор пользователя", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN")
            }
    )
    @RoleCheck("ROLE_ADMIN")
    @GetMapping("/users/{userId}/rentals")
    public ResponseEntity<List<Rental>> getRentalsByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(rentalService.getRentalsByUserId(userId));
    }
}