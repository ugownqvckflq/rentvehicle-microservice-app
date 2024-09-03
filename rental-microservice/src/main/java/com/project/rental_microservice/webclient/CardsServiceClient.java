package com.project.rental_microservice.webclient;

import com.project.rental_microservice.exceptions.CardCheckException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class CardsServiceClient {

    private final WebClient webClient;
    private String url = "http://localhost:8084/api/v1/cards";

    public CardsServiceClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(url).build();
    }

    public boolean checkCardExists(Long userId) {
        try {
            return Boolean.TRUE.equals(this.webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/exists").build())
                    .header("X-User-Id", userId.toString())
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (WebClientResponseException.NotFound e) {
            return false;
        } catch (Exception e) {
            throw new CardCheckException("Error while checking card existence", e);
        }
    }
}
