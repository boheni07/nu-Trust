package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;

public record TicketUpdateRequest(
    @NotBlank
    String status,

    String title,

    String description,

    String type,

    String priority,

    Long assignedSupporterId,

    Long approverId,

    String dueDate,

    String clientSatisfaction
) {
}
