package com.proj.payment_service.controller;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.dto.responses.BalanceResponse;
import com.proj.payment_service.dto.responses.SuccessResponse;
import com.proj.payment_service.service.CardService;
import com.project.rolechecker.RoleCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/card")
public class PaymentController {

    private final CardService cardService;

    public PaymentController(CardService cardService) {
        this.cardService = cardService;
    }

    private Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("User ID is missing in the request headers");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid user ID format");
        }
    }

    @Operation(summary = "Добавить новую карту",
            description = "Добавляет новую карту в аккаунт пользователя. Требуется ID пользователя в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123")
            })

    @PostMapping("/add-card")
    public ResponseEntity<SuccessResponse> addCard(HttpServletRequest request,
                                                   @Valid @RequestBody CardRequest cardRequest) {
        Long userId = extractUserIdFromHeader(request);
        cardService.addCard(userId, cardRequest);
        return ResponseEntity.ok(new SuccessResponse("Карта успешно добавлена", LocalDateTime.now()));
    }

    @Operation(summary = "Пополнить карту пользователя (только для админа)",
            description = "Пополняет карту пользователя. Требуется ID пользователя и роль в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123"),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN"),
                    @Parameter(name = "amount", description = "Сумма пополнения", required = true, example = "100.00")
            })

    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/add-funds")
    public ResponseEntity<SuccessResponse> addFunds(HttpServletRequest request,
                                                    @RequestParam @Parameter(description = "Сумма пополнения", required = true, example = "100.00") BigDecimal amount) {
        Long userId = extractUserIdFromHeader(request);
        cardService.addFunds(userId, amount);
        return ResponseEntity.ok(new SuccessResponse("Средства успешно добавлены", LocalDateTime.now()));
    }

    @Operation(summary = "Снять средства с карты пользователя (только для админа)",
            description = "Снимает средства с карты пользователя. Требуется ID пользователя и роль в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123"),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN"),
                    @Parameter(name = "amount", description = "Сумма снятия", required = true, example = "50.00")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/deduct-funds")
    public ResponseEntity<SuccessResponse> deductFunds(HttpServletRequest request,
                                                       @RequestParam @Parameter(description = "Сумма снятия", required = true, example = "50.00") BigDecimal amount) {
        Long userId = extractUserIdFromHeader(request);
        cardService.deductFunds(userId, amount);
        return ResponseEntity.ok(new SuccessResponse("Средства успешно сняты", LocalDateTime.now()));
    }

    @Operation(summary = "Получить баланс карты пользователя",
            description = "Получает текущий баланс карты пользователя. Требуется ID пользователя в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123")
            })

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(HttpServletRequest request) {
        Long userId = extractUserIdFromHeader(request);
        BigDecimal balance = cardService.getBalance(userId);
        return ResponseEntity.ok(new BalanceResponse(balance, LocalDateTime.now()));
    }
}