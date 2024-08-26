package com.vehicle.vehicle_microservice.dto;

import com.vehicle.vehicle_microservice.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;



@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {

    private Long id;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is mandatory")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 3, max = 100, message = "Password must be between 3 and 100 characters")
    private String password;

    private Set<Role> roles = new HashSet<>();


}