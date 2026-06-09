package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotNull;

public record NotificationPublishRequest(
    @NotNull String eventType,
    @NotNull Long targetUserId,
    Long ticketId,
    Long projectId,
    String payload
) {}
