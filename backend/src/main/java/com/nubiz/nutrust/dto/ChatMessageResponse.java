package com.nubiz.nutrust.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ChatMessageResponse(
    Long id,
    Long ticketId,
    Long userId,
    String content,
    List<String> imageUrl,
    boolean isEdited,
    LocalDateTime sentAt,
    LocalDateTime createdAt
) {
}
