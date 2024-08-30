package com.project.rental_microservice.webclient;

import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.exceptions.ExternalServiceException;
import com.project.rental_microservice.exceptions.VehicleNotFoundException;
import com.project.rental_microservice.exceptions.VehicleStatusUpdateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;


@Service
public class VehicleServiceClient {

    private final WebClient webClient;

    @Value("${vehicle-service.url}")
    private String url;

    public VehicleServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public Mono<VehicleDto> getVehicleByLicensePlate(String plate, String jwtToken) {
        return webClient.get()
                .uri("/license-plates/{licensePlate}", plate)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new VehicleNotFoundException("Vehicle with plate number " + plate + " not found"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ExternalServiceException("External service error!!!"))
                )
                .bodyToMono(VehicleDto.class)
                .retry(3) // Повторная попытка до 3 раз при ошибке
                .onErrorResume(e -> Mono.error(new ExternalServiceException("Failed to retrieve vehicle", e)));
    }

    public void setVehicleStatus(Long vehicleId, String status, String jwtToken) {
        webClient.post()
                .uri("/{id}/status/{status}", vehicleId, status)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> Mono.error(new VehicleStatusUpdateException("Failed to update vehicle status"))
                )
                .onStatus(
                        HttpStatusCode::is5xxServerError,
                        clientResponse -> Mono.error(new ExternalServiceException("External service error"))
                )
                .toBodilessEntity()
                .retry(3)
                .onErrorResume(e -> Mono.error(new ExternalServiceException("Failed to update vehicle status", e)))
                .block();
    }
}
