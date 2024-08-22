package com.project.notification_service.kafka;

import com.project.notification_service.dto.Rental;
import com.project.notification_service.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class RentalNotificationConsumer {

    private static final Logger logger = LoggerFactory.getLogger(RentalNotificationConsumer.class);

    private final NotificationService notificationService;

    public RentalNotificationConsumer(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "rental_topic", groupId = "notification_group")
    public void listenRentalEvents(Rental rental) {
        logger.info("Received rental event: {}", rental);

        try {
            if (rental.getStartTime() != null && rental.getEndTime() == null) {
                // Отправка уведомления о начале аренды
                notificationService.sendRentalStartEmail(rental);
                logger.info("Rental start notification sent for rental ID: {}", rental.getId());
            } else if (rental.getEndTime() != null) {
                // Отправка уведомления о завершении аренды
                notificationService.sendRentalEndEmail(rental);
                logger.info("Rental end notification sent for rental ID: {}", rental.getId());
            } else {
                logger.warn("Rental event does not have start or end time. Ignoring.");
            }

        } catch (Exception e) {
            logger.error("Error while processing rental event for rental ID: {}", rental.getId(), e);
        }
    }
}



