package com.proj.api_gateway.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthenticationRequest {

    @NotBlank
    private String username;
    @NotBlank
    private String password;


}
