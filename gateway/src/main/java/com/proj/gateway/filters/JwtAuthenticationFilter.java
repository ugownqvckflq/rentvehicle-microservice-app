package com.proj.gateway.filters;

import com.proj.gateway.util.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (path.startsWith("/auth/signin") || path.startsWith("/auth/signup")) {
            return chain.filter(exchange);
        }

        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap("JWT Token is missing".getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        String token = authHeader.substring(7);
        try {
            Claims claims = jwtService.validateToken(token); // Validate the token and get claims
            Long userId = jwtService.getUserIdFromToken(token); // Extract user ID from claims
            String userRole = claims.get("roles", String.class); // Extract user role from claims


            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Role", userRole)
                    .build();

            modifiedRequest.getHeaders().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (RuntimeException e) {
            // Log the exception for debugging
            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap("Invalid JWT Token".getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }
}