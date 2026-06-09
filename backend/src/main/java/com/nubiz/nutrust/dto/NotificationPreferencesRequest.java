package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.Pattern;

public record NotificationPreferencesRequest(
    Boolean inAppEnabled,
    Boolean pushEnabled,
    Boolean emailEnabled,
    Boolean soundEnabled,
    Boolean badgeEnabled,

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    String quietHoursStart,

    @Pattern(regexp = "^([0-1]?[0-9]|2[0-3]):[0-5][0-9]$")
    String quietHoursEnd,

    String quietHoursMode
) {
}
