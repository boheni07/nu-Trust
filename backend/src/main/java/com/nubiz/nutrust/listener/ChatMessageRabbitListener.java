package com.nubiz.nutrust.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nubiz.nutrust.dto.ChatMessageResponse;
import com.nubiz.nutrust.service.ChatRabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatMessageRabbitListener {

    private static final String QUEUE = "nutrust.chat";
    private static final String TOPIC_FORMAT = "/topic/ticket.%s";

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = QUEUE)
    public void onChatMessage(String json) {
        try {
            var msg = objectMapper.readValue(json, ChatRabbitMqService.ChatPublishMessage.class);

            ChatMessageResponse response = new ChatMessageResponse(
                null,
                msg.ticketId(),
                msg.senderId(),
                msg.content(),
                null,
                false,
                LocalDateTime.now(),
                LocalDateTime.now()
            );

            String destination = String.format(TOPIC_FORMAT, msg.ticketId());
            messagingTemplate.convertAndSend(destination, response);
        } catch (Exception e) {
            log.error("Failed to process chat message from RabbitMQ", e);
        }
    }
}
