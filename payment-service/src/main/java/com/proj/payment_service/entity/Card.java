package com.proj.payment_service.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @NotBlank
    private Long id;
    @NotBlank
    private Long userId;
    @NotBlank
    private String cardNumber;
    @NotBlank
    private String expiryDate;
    @NotBlank
    private String cvv;
    @NotBlank
    private Double balance;
}