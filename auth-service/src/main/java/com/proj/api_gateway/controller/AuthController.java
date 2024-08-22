package com.proj.api_gateway.controller;

import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.exceptions.UserBannedException;
import com.proj.api_gateway.exceptions.UserNotFoundException;
import com.proj.api_gateway.payload.request.AuthenticationRequest;
import com.proj.api_gateway.payload.request.SignupRequest;
import com.proj.api_gateway.payload.response.ErrorResponse;
import com.proj.api_gateway.payload.response.JwtResponse;
import com.proj.api_gateway.security.jwt.JwtUtils;
import com.proj.api_gateway.security.services.UserDetailsImpl;
import com.proj.api_gateway.security.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final UserService userService;

    @Operation(summary = "Аутентификация пользователя", description = "Аутентифицирует пользователя по имени пользователя и паролю.")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest loginRequest) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        // Проверка на забаненность пользователя
        if (user.getBanned()) {
            throw new UserBannedException("You are banned. Login failed.");
        }
        String jwt = jwtUtils.generateJwtToken(authentication, user.getId());
        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @Operation(summary = "Регистрация нового пользователя", description = "Регистрирует нового пользователя с указанными данными.")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerSimpleUser(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }



}