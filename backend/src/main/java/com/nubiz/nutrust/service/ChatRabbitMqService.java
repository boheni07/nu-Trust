package com.nubiz.nutrust.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRabbitMqService {

    private static final String EXCHANGE = "topic.chat";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publish(Long ticketId, Long senderId, String content) {
        try {
            String routingKey = "chat.ticket." + ticketId;
            var message = new ChatPublishMessage(ticketId, senderId, content);
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, json);
        } catch (Exception e) {
            log.error("Chat RabbitMQ publish failed: ticketId={}, senderId={}", ticketId, senderId, e);
            throw new RuntimeException("Chat RabbitMQ publish failed", e);
        }
    }

    public record ChatPublishMessage(Long ticketId, Long senderId, String content) {}
}
