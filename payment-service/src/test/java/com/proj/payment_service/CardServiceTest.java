package com.proj.payment_service;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.entity.Card;
import com.proj.payment_service.exceptions.CardAlreadyExistsException;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.service.impl.CardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cardService = new CardServiceImpl(cardRepository);

    }

    @Test
    void testAddCard_Success() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setCardNumber("4434567812345678");
        cardRequest.setExpiryDate("11/11");
        cardRequest.setCvv("888");

        when(cardRepository.findByCardNumber(cardRequest.getCardNumber())).thenReturn(Optional.empty());

        cardService.addCard(1L, cardRequest);

        verify(cardRepository, times(1)).save(any(Card.class));
    }

    @Test
    void testAddCard_CardAlreadyExists() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setCardNumber("1234567812345678");

        when(cardRepository.findByCardNumber(cardRequest.getCardNumber())).thenReturn(Optional.of(new Card()));

        assertThatThrownBy(() -> cardService.addCard(1L, cardRequest))
                .isInstanceOf(CardAlreadyExistsException.class)
                .hasMessage("Card already exists");
    }

}
