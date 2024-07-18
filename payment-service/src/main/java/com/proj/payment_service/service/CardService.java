package com.proj.payment_service.service;

import com.proj.payment_service.dto.CardRequest;

import com.proj.payment_service.entity.Card;
import com.proj.payment_service.repository.CardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class CardService {

    @Autowired
    private CardRepository cardRepository;

    @Value("${encryption.password}")
    private String encryptionPassword;

    @Value("${encryption.salt}")
    private String encryptionSalt;

    public Card addCard(Long userId, CardRequest cardRequest) {
        Optional<Card> existingCard = cardRepository.findByCardNumber(cardRequest.getCardNumber());
        if (existingCard.isPresent()) {
            throw new RuntimeException("Card already exists");
        }

        Card card = new Card();
        card.setUserId(userId);
        card.setCardNumber(cardRequest.getCardNumber());
        card.setExpiryDate(cardRequest.getExpiryDate());
        card.setCvv(encryptCvv(cardRequest.getCvv()));
        card.setBalance(0.0);

        return cardRepository.save(card);
    }

    private String encryptCvv(String cvv) {
        return Encryptors.text(encryptionPassword, encryptionSalt).encrypt(cvv);
    }

    public Card getCardByUserId(Long userId) {
        return cardRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Card not found for user: " + userId));
    }

    @Transactional
    public void addFunds(Long userId, Double amount) {
        Card card = getCardByUserId(userId);
        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);
    }

    @Transactional
    public void deductFunds(Long userId, Double amount) {
        Card card = getCardByUserId(userId);
        if (card.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        card.setBalance(card.getBalance() - amount);
        cardRepository.save(card);
    }

    public Double getBalance(Long userId) {
        Card card = getCardByUserId(userId);
        return card.getBalance();
    }
}