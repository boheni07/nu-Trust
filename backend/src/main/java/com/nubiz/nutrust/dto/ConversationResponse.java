package com.nubiz.nutrust.dto;

import java.time.LocalDateTime;

public record ConversationResponse(
    Long ticketId,
    String ticketTitle,
    String lastMessageContent,
    LocalDateTime lastMessageAt,
    long unreadCount
) {
}
