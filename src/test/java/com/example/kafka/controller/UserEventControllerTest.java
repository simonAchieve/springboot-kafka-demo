package com.example.kafka.controller;

import com.example.kafka.service.KafkaProducerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserEventController.class)
class UserEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    void testHealthEndpoint() throws Exception {
        mockMvc.perform(get("/api/users/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("User Event Service is running"));
    }

    @Test
    void testPublishUserEvent() throws Exception {
        String requestBody = "{\"name\":\"John Doe\",\"email\":\"john.doe@example.com\"}";

        mockMvc.perform(post("/api/users/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isCreated());
    }
}
