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


    private String username;


    private String email;


    private String password;


    private Set<Role> roles = new HashSet<>();

    public User(String username, String password){
        this.username = username;
        this.password = password;
    }

}