package com.proj.gateway.config;


import com.proj.gateway.filters.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.reactive.CorsWebFilter;


@Configuration
public class GatewayConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public GatewayConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(r -> r.path("/api/v1/auth/**")
                        .uri("lb://AUTH-SERVICE"))
                .route(r -> r.path("/api/v1/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://AUTH-SERVICE"))
                .route(r -> r.path("/api/v1/vehicles/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://VEHICLE-SERVICE"))
                .route(r -> r.path("/api/v1/rentals/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://RENTAL-SERVICE"))
                .route(r -> r.path("/api/v1/cards/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://PAYMENT-SERVICE"))
                .build();
    }


}