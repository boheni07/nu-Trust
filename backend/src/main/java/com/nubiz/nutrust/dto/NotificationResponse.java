package com.nubiz.nutrust.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String eventId,
    String payload,
    String sentVia,
    LocalDateTime createdAt,
    boolean isRead
) {
}
