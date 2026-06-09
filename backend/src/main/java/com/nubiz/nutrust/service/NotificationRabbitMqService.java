package com.nubiz.nutrust.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationRabbitMqService {

    private static final String EXCHANGE = "topic.notification";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public void publish(String eventType, Long targetUserId, Long ticketId, Long projectId, String payload) {
        try {
            String routingKey = String.format("notification.%s.user.%s", ticketId != null ? ticketId : 0L, targetUserId);
            var message = new NotificationPublishMessage(eventType, targetUserId, ticketId, projectId, payload);
            String json = objectMapper.writeValueAsString(message);
            rabbitTemplate.convertAndSend(EXCHANGE, routingKey, json);
        } catch (Exception e) {
            log.error("Notification RabbitMQ publish failed: eventType={}, userId={}", eventType, targetUserId, e);
            throw new RuntimeException("Notification RabbitMQ publish failed", e);
        }
    }

    public record NotificationPublishMessage(
        String eventType,
        Long targetUserId,
        Long ticketId,
        Long projectId,
        String payload
    ) {}
}
