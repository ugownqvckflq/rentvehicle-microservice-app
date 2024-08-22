package com.proj.payment_service;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.dto.responses.BalanceResponse;
import com.proj.payment_service.dto.responses.SuccessResponse;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.service.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentControllerIntegrationTest { //TODO test

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CardService cardService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(cardService, "encryptionPassword", "testPassword");
        ReflectionTestUtils.setField(cardService, "encryptionSalt", "testSalt");
    }

    @Test
    public void testAddCard() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "1");

        CardRequest cardRequest = new CardRequest();
        cardRequest.setCardNumber("1234567812345678");
        cardRequest.setExpiryDate("12/23");
        cardRequest.setCvv("123");

        HttpEntity<CardRequest> request = new HttpEntity<>(cardRequest, headers);
        ResponseEntity<SuccessResponse> response = restTemplate.exchange("/card/add-card", HttpMethod.POST, request, SuccessResponse.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getMessage()).isEqualTo("Card added successfully");
    }

    @Test
    public void testGetBalance() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-User-Id", "1");

        ResponseEntity<BalanceResponse> response = restTemplate.exchange("/card/balance", HttpMethod.GET, new HttpEntity<>(headers), BalanceResponse.class);

        assertThat(response.getStatusCodeValue()).isEqualTo(200);
        assertThat(response.getBody().getBalance()).isEqualTo(BigDecimal.ZERO);
    }
}
