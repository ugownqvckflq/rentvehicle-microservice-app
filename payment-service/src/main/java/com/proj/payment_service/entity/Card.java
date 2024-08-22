package com.proj.payment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "cards")
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Balance must not be null")
    private BigDecimal balance;

    @Column(nullable = false, length = 19)
    @NotBlank(message = "Card number must not be blank")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits")
    private String cardNumber;

    @Column(nullable = false, length = 4)
    @NotBlank(message = "CVV must not be blank")
    private String cvv;

    @Column(nullable = false, length = 5)
    @NotBlank(message = "Expiry date must not be blank")
    @Pattern(regexp = "(0[1-9]|1[0-2])\\/\\d{2}", message = "Expiry date must be in MM/YY format")
    private String expiryDate;

    @Column(nullable = false)
    @NotNull(message = "User ID must not be null")
    private Long userId;
}