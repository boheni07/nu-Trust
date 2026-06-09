package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record ChatMessageSend(
    @NotNull
    Long ticketId,

    @NotBlank
    String content,

    List<String> imageUrl
) {
}
