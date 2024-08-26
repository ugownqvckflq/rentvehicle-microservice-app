package com.proj.payment_service.dto.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class SuccessResponse {
    private String message;
    private LocalDateTime timestamp;
}
