package com.project.rental_microservice.webclient;

import com.project.rental_microservice.dto.VehicleDto;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class VehicleServiceClient {

    private final WebClient webClient;

    public VehicleServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8082").build();
    }

    public Mono<VehicleDto> getVehicleByLicensePlate(String plate, String jwtToken) {
        return webClient.get()
                .uri("/vehicles/plate/{plate}", plate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .bodyToMono(VehicleDto.class);
    }

    public void setVehicleStatus(Long id, String status, String jwtToken) {
        try {
            webClient.post()
                    .uri("/set-status/{id}/{status}", id, status)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> Mono.empty())
                    .bodyToMono(Void.class)
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to set vehicle status", e);
        }
    }
}
