package com.proj.api_gateway;

import com.proj.api_gateway.controller.AuthController;
import com.proj.api_gateway.entity.User;
import com.proj.api_gateway.exceptions.UserBannedException;
import com.proj.api_gateway.exceptions.UserNotFoundException;
import com.proj.api_gateway.payload.request.AuthenticationRequest;
import com.proj.api_gateway.payload.response.JwtResponse;
import com.proj.api_gateway.security.jwt.JwtUtils;
import com.proj.api_gateway.security.services.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    public AuthControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAuthenticateUser_Success() {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setBanned(false);

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(java.util.Optional.of(user));
        when(jwtUtils.generateJwtToken(authentication, user.getId())).thenReturn("testToken");

        ResponseEntity<?> response = authController.authenticateUser(authRequest);

        assertEquals(ResponseEntity.ok(new JwtResponse("testToken")), response);
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticateUser_UserBanned() {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("bannedUser");
        authRequest.setPassword("password");

        User bannedUser = new User();
        bannedUser.setId(1L);
        bannedUser.setUsername("bannedUser");
        bannedUser.setBanned(true);

        when(userService.findByUsername("bannedUser")).thenReturn(java.util.Optional.of(bannedUser));

        assertThrows(UserBannedException.class, () -> {
            authController.authenticateUser(authRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }

    @Test
    public void testAuthenticateUser_UserNotFound() {
        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.setUsername("nonExistentUser");
        authRequest.setPassword("password");

        when(userService.findByUsername("nonExistentUser")).thenReturn(java.util.Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            authController.authenticateUser(authRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
    }
}
