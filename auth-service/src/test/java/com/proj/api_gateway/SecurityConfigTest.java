package com.proj.api_gateway;

import com.proj.api_gateway.config.SecurityConfig;
import com.proj.api_gateway.security.jwt.JwtAuthenticationFilter;
import com.proj.api_gateway.security.jwt.JwtUtils;
import com.proj.api_gateway.security.services.UserDetailsServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

public class SecurityConfigTest {

    private JwtUtils jwtUtils = mock(JwtUtils.class);
    private UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);

    private SecurityConfig securityConfig = new SecurityConfig(jwtUtils, userDetailsService);

    @Test
    public void testJwtAuthenticationFilter() {
        JwtAuthenticationFilter filter = securityConfig.jwtAuthenticationFilter();
        assertNotNull(filter);
    }

    @Test
    public void testAuthenticationManager() throws Exception {
        AuthenticationConfiguration authConfig = mock(AuthenticationConfiguration.class);
        AuthenticationManager authManager = mock(AuthenticationManager.class);
        Mockito.when(authConfig.getAuthenticationManager()).thenReturn(authManager);

        AuthenticationManager result = securityConfig.authenticationManager(authConfig);
        assertNotNull(result);
    }

    @Test
    public void testPasswordEncoder() {
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        assertNotNull(encoder);
        assert(encoder instanceof BCryptPasswordEncoder);
    }
}