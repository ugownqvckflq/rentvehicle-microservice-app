package com.proj.payment_service.controller;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.dto.responses.BalanceResponse;
import com.proj.payment_service.dto.responses.SuccessResponse;
import com.proj.payment_service.service.CardService;
import com.project.rolechecker.RoleCheck;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/cards")
@RequiredArgsConstructor
public class PaymentController {

    private final CardService cardService;


    @Operation(summary = "Добавить новую карту",
            description = "Добавляет новую карту в аккаунт пользователя. Требуется ID пользователя в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123")
            })
    @PostMapping
    public ResponseEntity<SuccessResponse> addCard(HttpServletRequest request,
                                                   @Valid @RequestBody CardRequest cardRequest) {
        Long userId = CardService.extractUserIdFromHeader(request);
        cardService.addCard(userId, cardRequest);
        return ResponseEntity.ok(new SuccessResponse("The card has been added successfully", LocalDateTime.now()));
    }

    @Operation(summary = "Пополнить карту пользователя (только для админа)",
            description = "Пополняет карту пользователя. Требуется ID пользователя и роль в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123"),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN"),
                    @Parameter(name = "amount", description = "Сумма пополнения", required = true, example = "100.00")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/funds")
    public ResponseEntity<SuccessResponse> addFunds(HttpServletRequest request,
                                                    @RequestParam @Parameter(description = "Сумма пополнения", required = true, example = "100.00") BigDecimal amount) {
        Long userId = CardService.extractUserIdFromHeader(request);
        cardService.addFunds(userId, amount);
        return ResponseEntity.ok(new SuccessResponse("Funds have been added successfully", LocalDateTime.now()));
    }

    @Operation(summary = "Снять средства с карты пользователя (только для админа)",
            description = "Снимает средства с карты пользователя. Требуется ID пользователя и роль в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123"),
                    @Parameter(name = "X-User-Role", description = "Роль пользователя", required = true, in = ParameterIn.HEADER, example = "ROLE_ADMIN"),
                    @Parameter(name = "amount", description = "Сумма снятия", required = true, example = "50.00")
            })
    @RoleCheck("ROLE_ADMIN")
    @PostMapping("/funds/deduct")
    public ResponseEntity<SuccessResponse> deductFunds(HttpServletRequest request,
                                                       @RequestParam @Parameter(description = "Сумма снятия", required = true, example = "50.00") BigDecimal amount) {
        Long userId = CardService.extractUserIdFromHeader(request);
        cardService.deductFunds(userId, amount);
        return ResponseEntity.ok(new SuccessResponse("Funds have been successfully withdrawn", LocalDateTime.now()));
    }

    @Operation(summary = "Получить баланс карты пользователя",
            description = "Получает текущий баланс карты пользователя. Требуется ID пользователя в заголовке.",
            parameters = {
                    @Parameter(name = "X-User-Id", description = "Идентификатор пользователя", required = true, in = ParameterIn.HEADER, example = "123")
            })
    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance(HttpServletRequest request) {
        Long userId = CardService.extractUserIdFromHeader(request);
        BigDecimal balance = cardService.getBalance(userId);
        return ResponseEntity.ok(new BalanceResponse(balance, LocalDateTime.now()));
    }
}