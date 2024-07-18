package com.proj.payment_service.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CardRequest {

    @NotBlank
    private String cardNumber;
    @NotBlank
    private String expiryDate;
    @NotBlank
    private String cvv;
}
