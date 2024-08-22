package com.project.rental_microservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.rental_microservice.dto.VehicleDto;
import com.project.rental_microservice.exceptions.ExternalServiceException;
import com.project.rental_microservice.webclient.VehicleServiceClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class VehicleServiceClientTest {

    private MockWebServer mockWebServer;
    private VehicleServiceClient vehicleServiceClient;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        WebClient.Builder webClientBuilder = WebClient.builder().baseUrl(mockWebServer.url("/").toString());
        vehicleServiceClient = new VehicleServiceClient(webClientBuilder);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockWebServer.shutdown();
    }


    @Test
    void testGetVehicleByLicensePlateNotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(HttpStatus.NOT_FOUND.value())
                .setBody("{\"message\":\"Vehicle not found\"}")
                .addHeader(HttpHeaders.CONTENT_TYPE, "application/json"));

        assertThatThrownBy(() -> vehicleServiceClient.getVehicleByLicensePlate("XYZ999", "token").block())
                .isInstanceOf(ExternalServiceException.class)
                .hasMessageContaining("Failed to retrieve vehicle");
    }
}
