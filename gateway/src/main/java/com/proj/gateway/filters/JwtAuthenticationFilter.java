package com.proj.gateway.filters;

import com.proj.gateway.util.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements GatewayFilter {

    private final JwtService jwtService;


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
            Claims claims = jwtService.validateToken(token);
            Long userId = jwtService.getUserIdFromToken(token);
            String userRole = claims.get("roles", String.class);

            ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-User-Id", userId.toString())
                    .header("X-User-Role", userRole)
                    .build();

            modifiedRequest.getHeaders().forEach((key, value) -> {
                System.out.println(key + ": " + value);
            });

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } catch (RuntimeException e) {

            e.printStackTrace();
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory().wrap("Invalid JWT Token".getBytes());
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
    }
}