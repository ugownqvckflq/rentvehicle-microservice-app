package com.project.rental_microservice.config.kafka;

import com.project.rental_microservice.entity.Rental;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    @Value("${topic.name}")
    private String TOPIC;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(Rental rental) {
        kafkaTemplate.send(TOPIC, rental);
    }
}
