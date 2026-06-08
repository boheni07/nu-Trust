package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TicketCommentCreateRequest(
    @NotNull
    Long ticketId,

    @NotBlank
    String content,

    Long parentId
) {
}
