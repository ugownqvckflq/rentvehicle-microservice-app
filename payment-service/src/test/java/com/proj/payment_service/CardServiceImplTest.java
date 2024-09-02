package com.proj.payment_service;

import com.proj.payment_service.dto.requests.CardRequest;
import com.proj.payment_service.entity.Card;
import com.proj.payment_service.exceptions.CardAlreadyExistsException;
import com.proj.payment_service.exceptions.CardNotFoundException;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.service.CardServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;


public class CardServiceImplTest {

    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardServiceImpl cardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testAddCard_cardAlreadyExists() {
        CardRequest cardRequest = new CardRequest();
        cardRequest.setCardNumber("1234567812345678");
        when(cardRepository.findByCardNumber(anyString())).thenReturn(Optional.of(new Card()));
        Assertions.assertThrows(CardAlreadyExistsException.class, () -> {
            cardService.addCard(1L, cardRequest);
        });
    }

    @Test
    void testGetCardByUserId_success() {
        Card card = new Card();
        card.setUserId(1L);
        when(cardRepository.findByUserId(anyLong())).thenReturn(Optional.of(card));
        Card result = cardService.getCardByUserId(1L);
        Assertions.assertEquals(1L, result.getUserId());
    }

    @Test
    void testGetCardByUserId_cardNotFound() {
        when(cardRepository.findByUserId(anyLong())).thenReturn(Optional.empty());
        Assertions.assertThrows(CardNotFoundException.class, () -> {
            cardService.getCardByUserId(1L);
        });
    }
}
