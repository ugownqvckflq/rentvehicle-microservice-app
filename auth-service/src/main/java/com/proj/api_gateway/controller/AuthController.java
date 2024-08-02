package com.proj.api_gateway.controller;

import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.payload.request.AuthenticationRequest;
import com.proj.api_gateway.payload.request.SignupRequest;
import com.proj.api_gateway.payload.response.JwtResponse;
import com.proj.api_gateway.repository.RoleRepository;
import com.proj.api_gateway.security.jwt.JwtUtils;
import com.proj.api_gateway.security.services.UserDetailsImpl;
import com.proj.api_gateway.security.services.UserDetailsServiceImpl;
import com.proj.api_gateway.security.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;



    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest loginRequest) {
        // Аутентификация пользователя
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        User user = userService.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long userId = user.getId();

        // Генерация JWT с userId
        String jwt = jwtUtils.generateJwtToken(authentication, userId);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        userService.registerSimpleUser(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                signUpRequest.getPassword());
        return ResponseEntity.ok("User registered successfully!");
    }


}