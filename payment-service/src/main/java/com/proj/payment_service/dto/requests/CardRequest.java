package com.proj.payment_service.dto.requests;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CardRequest {

    @NotBlank(message = "Card number is mandatory")
    @Size(min = 16, max = 19, message = "Card number must be between 16 and 19 digits")
    @Pattern(regexp = "\\d+", message = "Card number must be numeric")
    private String cardNumber;

    @NotBlank(message = "Expiry date is mandatory")
    @Pattern(regexp = "(0[1-9]|1[0-2])/([0-9]{2})", message = "Expiry date should be in MM/YY format")
    private String expiryDate;

    @NotBlank(message = "CVV is mandatory")
    @Size(min = 3,max = 3, message = "CVV must be 3 digits")
    private String cvv;
}
