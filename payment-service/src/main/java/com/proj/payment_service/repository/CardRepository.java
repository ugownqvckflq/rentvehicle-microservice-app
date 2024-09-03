package com.proj.payment_service.repository;

import com.proj.payment_service.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserId(Long userId);
    Optional<Card> findByCardNumber(String cardNumber);
    boolean existsByUserId(Long userId);
}
