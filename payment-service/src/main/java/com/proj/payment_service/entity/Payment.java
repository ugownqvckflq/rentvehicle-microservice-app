package com.proj.payment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "User ID must not be null")
    private Long userId;

    @Column(nullable = false)
    @NotNull(message = "Amount must not be null")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    @Column(nullable = false)
    @NotBlank(message = "Description must not be blank")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Created At must not be null")
    private LocalDateTime createdAt;
}