package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCreateRequest(
    @NotNull
    Long projectId,

    @NotBlank
    String title,

    String description,

    String type,

    String priority,

    Long assignedSupporterId,

    Long approverId,

    String dueDate
) {
}
