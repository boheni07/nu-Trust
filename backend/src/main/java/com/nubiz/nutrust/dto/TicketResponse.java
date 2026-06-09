package com.nubiz.nutrust.dto;

import com.nubiz.nutrust.entity.Ticket;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record TicketResponse(
    Long id,
    Long projectId,
    String title,
    String description,
    String type,
    String priority,
    String status,
    String currentStatus,
    Long assignedSupporterId,
    String assignedSupporterName,
    Long approverId,
    String approverName,
    LocalDate dueDate,
    LocalDate actualCompletionDate,
    String clientSatisfaction,
    Integer progress,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static TicketResponse from(Ticket t) {
        return TicketResponse.builder()
            .id(t.getId())
            .projectId(t.getProject().getId())
            .title(t.getTitle())
            .description(t.getDescription())
            .type(t.getType())
            .priority(t.getPriority())
            .status(t.getStatus())
            .currentStatus(t.getCurrentStatus())
            .assignedSupporterId(t.getAssignedSupporter() != null ? t.getAssignedSupporter().getId() : null)
            .assignedSupporterName(t.getAssignedSupporter() != null ? t.getAssignedSupporter().getName() : null)
            .approverId(t.getApprover() != null ? t.getApprover().getId() : null)
            .approverName(t.getApprover() != null ? t.getApprover().getName() : null)
            .dueDate(t.getDueDate())
            .actualCompletionDate(t.getActualCompletionDate())
            .clientSatisfaction(t.getClientSatisfaction())
            .progress(t.getProgress())
            .createdAt(t.getCreatedAt())
            .updatedAt(t.getUpdatedAt())
            .build();
    }
}
