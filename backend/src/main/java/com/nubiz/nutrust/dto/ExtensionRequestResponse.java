package com.nubiz.nutrust.dto;

import java.time.LocalDateTime;

public record ExtensionRequestResponse(
    Long id,
    Long ticketId,
    Long userId,
    String reason,
    Integer requestedExtendDays,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}
