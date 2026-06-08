package com.nubiz.nutrust.dto;

import com.nubiz.nutrust.entity.Ticket;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record TicketSummaryResponse(
    Long id,
    String title,
    String type,
    String priority,
    String status,
    String assignedSupporterName,
    LocalDateTime createdAt
) {
    public static TicketSummaryResponse from(Ticket t) {
        return TicketSummaryResponse.builder()
            .id(t.getId())
            .title(t.getTitle())
            .type(t.getType())
            .priority(t.getPriority())
            .status(t.getStatus())
            .assignedSupporterName(t.getAssignedSupporter() != null ? t.getAssignedSupporter().getName() : null)
            .createdAt(t.getCreatedAt())
            .build();
    }
}
