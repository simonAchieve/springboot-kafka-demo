package com.example.kafka.controller;

import com.example.kafka.model.UserEvent;
import com.example.kafka.service.KafkaProducerService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserEventController {

    private final KafkaProducerService kafkaProducerService;

    public UserEventController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/events")
    public ResponseEntity<String> publishUserEvent(@Valid @RequestBody UserEventRequest request) {
        UserEvent userEvent = UserEvent.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setName(request.getName())
                .setEmail(request.getEmail())
                .setTimestamp(System.currentTimeMillis())
                .build();

        kafkaProducerService.sendUserEvent(userEvent);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body("User event published successfully with id: " + userEvent.getId());
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Event Service is running");
    }

    // DTO for request
    public static class UserEventRequest {
        @NotBlank(message = "Name is required")
        private String name;

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}
