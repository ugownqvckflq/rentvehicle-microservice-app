package com.proj.payment_service.service;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.entity.Card;
import jakarta.servlet.http.HttpServletRequest;

import java.math.BigDecimal;

public interface CardService {

    Boolean cardExists(Long userId);

    void addCard(Long userId, CardRequest cardRequest);

    Card getCardByUserId(Long userId);

    void addFunds(Long userId, BigDecimal amount);

    void deductFunds(Long userId, BigDecimal amount);

    BigDecimal getBalance(Long userId);

    static Long extractUserIdFromHeader(HttpServletRequest request) {
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null) {
            throw new IllegalArgumentException("User ID is missing in the request headers");
        }
        try {
            return Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid user ID format");
        }
    }
}