package com.example.kafka.service;

import com.example.kafka.model.UserEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListener(topics = "${kafka.topic.user-events}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumeUserEvent(
            @Payload UserEvent userEvent,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        logger.info("Received user event: {} from partition: {} with offset: {}", 
            userEvent, partition, offset);
        
        // Process the user event
        processUserEvent(userEvent);
    }

    private void processUserEvent(UserEvent userEvent) {
        // Add your business logic here
        logger.info("Processing user event for user: {} with email: {}", 
            userEvent.getName(), userEvent.getEmail());
    }
}
