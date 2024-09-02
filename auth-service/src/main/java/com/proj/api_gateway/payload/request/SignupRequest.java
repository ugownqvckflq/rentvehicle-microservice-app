package com.proj.api_gateway.payload.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Set;

@Data
public class SignupRequest {

    @NotBlank(message = "Username cannot be empty.")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters long.")
    private String username;

    @NotBlank(message = "Email cannot be empty.")
    @Size(max = 50, message = "Email must be less than 50 characters long.")
    @Email(message = "Email should be valid and follow the standard email format.")
    private String email;

    @NotBlank(message = "Password cannot be empty.")
    @Size(min = 4, max = 40, message = "Password must be between 4 and 40 characters long.")
    private String password;
}
