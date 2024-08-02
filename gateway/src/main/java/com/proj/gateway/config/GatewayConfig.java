package com.proj.gateway.config;


import com.proj.gateway.filters.JwtAuthenticationFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


@Configuration
public class GatewayConfig {

    @Value("${jwt.secret}")
    private String secretKey;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /*
     @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder, JwtAuthenticationFilter jwtAuthenticationFilter) {
        return builder.routes()
                .route(r -> r.path("/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8088"))
                .route(r -> r.path("/auth/**")
                        .uri("http://localhost:8088"))
                .route(r -> r.path("/vehicles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8082"))
                .route(r -> r.path("/rentals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8083"))
                .route(r -> r.path("/payments/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("http://localhost:8084"))
                .build();
    }
     */


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route(r -> r.path("/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route(r -> r.path("/vehicles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://VEHICLE-SERVICE"))
                .route(r -> r.path("/rentals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://RENTAL-SERVICE"))
                .route(r -> r.path("/payments/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }


}