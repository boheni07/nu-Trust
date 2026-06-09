package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NotificationSubscriptionRequest(
    @NotBlank
    String eventType,

    @NotBlank
    String channel,

    @NotNull
    Boolean isActive
) {
}
