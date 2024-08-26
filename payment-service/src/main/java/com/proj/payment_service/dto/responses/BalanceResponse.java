package com.proj.payment_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BalanceResponse {
    private BigDecimal balance;
    private LocalDateTime timestamp;
}
