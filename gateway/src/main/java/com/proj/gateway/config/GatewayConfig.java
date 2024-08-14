package com.proj.gateway.config;


import com.proj.gateway.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class GatewayConfig {

    @Value("${jwt.secret}")
    private String secretKey;


    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


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
                .route(r -> r.path("/card/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }


}