package com.example.kafka.service;

import com.example.kafka.model.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${kafka.topic.user-events}")
    private String userEventsTopic;

    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendUserEvent(UserEvent userEvent) {
        logger.info("Sending user event: {}", userEvent);
        
        CompletableFuture<SendResult<String, UserEvent>> future = 
            kafkaTemplate.send(userEventsTopic, userEvent.getId().toString(), userEvent);
        
        future.whenComplete((result, ex) -> {
            if (ex == null) {
                logger.info("Successfully sent user event with id: {} to partition: {}", 
                    userEvent.getId(), result.getRecordMetadata().partition());
            } else {
                logger.error("Failed to send user event with id: {}", userEvent.getId(), ex);
            }
        });
    }
}
