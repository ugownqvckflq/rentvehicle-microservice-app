package com.proj.payment_service.service;

import com.proj.payment_service.dto.requests.CardRequest;

import com.proj.payment_service.entity.Card;
import com.proj.payment_service.exceptions.CardAlreadyExistsException;
import com.proj.payment_service.exceptions.CardNotFoundException;
import com.proj.payment_service.repository.CardRepository;
import com.proj.payment_service.service.CardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    @Value("${encryption.salt}")
    private String encryptionSalt;


    @Transactional
    public void addCard(Long userId, CardRequest cardRequest) {
        Optional<Card> existingCard = cardRepository.findByCardNumber(cardRequest.getCardNumber());
        if (existingCard.isPresent()) {
            throw new CardAlreadyExistsException("Card already exists");
        }

        Card card = new Card();
        card.setUserId(userId);
        card.setCardNumber(cardRequest.getCardNumber());
        card.setExpiryDate(cardRequest.getExpiryDate());
        card.setCvv(encryptCvv(cardRequest.getCvv()));
        card.setBalance(BigDecimal.valueOf(15.0));

        cardRepository.save(card);
    }

    private String encryptCvv(String cvv) {
        return Encryptors.text(encryptionPassword, encryptionSalt).encrypt(cvv);
    }

    public Card getCardByUserId(Long userId) {
        return cardRepository.findByUserId(userId)
                .orElseThrow(() -> new CardNotFoundException("Card not found for user: " + userId));
    }

    @Transactional
    public void addFunds(Long userId, BigDecimal amount) {
        Card card = getCardByUserId(userId);
        card.setBalance(card.getBalance().add(amount));
        cardRepository.save(card);
    }

    @Transactional
    public void deductFunds(Long userId, BigDecimal amount) {
        Card card = getCardByUserId(userId);
        if (card.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }
        card.setBalance(card.getBalance().subtract(amount));
        cardRepository.save(card);
    }

    public BigDecimal getBalance(Long userId) {
        Card card = getCardByUserId(userId);
        return card.getBalance();
    }
}