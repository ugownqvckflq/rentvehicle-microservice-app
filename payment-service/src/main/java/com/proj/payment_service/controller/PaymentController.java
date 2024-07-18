package com.proj.payment_service.controller;

import com.proj.payment_service.dto.CardRequest;
import com.proj.payment_service.service.CardService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/card")
public class PaymentController {

    private final CardService cardService;

    public PaymentController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add-card")
    public ResponseEntity<?> addCard(Authentication authentication, @Valid @RequestBody CardRequest cardRequest) {
        Long userId = (Long) authentication.getPrincipal(); // Извлечение userId из токена
        cardService.addCard(userId, cardRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/add-funds")
    public ResponseEntity<?> addFunds(Authentication authentication, @RequestParam Double amount) {
        Long userId = (Long) authentication.getPrincipal();
        cardService.addFunds(userId, amount);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/deduct-funds")
    public ResponseEntity<?> deductFunds(Authentication authentication, @RequestParam Double amount) {
        Long userId = (Long) authentication.getPrincipal();
        cardService.deductFunds(userId, amount);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        Double balance = cardService.getBalance(userId);
        return ResponseEntity.ok(balance);
    }
}