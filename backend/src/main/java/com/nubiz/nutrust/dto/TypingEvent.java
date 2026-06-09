package com.nubiz.nutrust.dto;

import java.time.LocalDateTime;

public record TypingEvent(
    Long ticketId,
    Long userId,
    String userName
) {}
