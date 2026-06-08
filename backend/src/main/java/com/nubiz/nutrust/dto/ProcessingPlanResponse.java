package com.nubiz.nutrust.dto;

import com.nubiz.nutrust.entity.ProcessingPlan;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProcessingPlanResponse(
    Long id,
    Long ticketId,
    Long writerId,
    String writerName,
    String title,
    String content,
    String status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static ProcessingPlanResponse from(ProcessingPlan p) {
        return ProcessingPlanResponse.builder()
            .id(p.getId())
            .ticketId(p.getTicket().getId())
            .writerId(p.getWriter().getId())
            .writerName(p.getWriter().getName())
            .title(p.getTitle())
            .content(p.getContent())
            .status(p.getStatus())
            .createdAt(p.getCreatedAt())
            .updatedAt(p.getUpdatedAt())
            .build();
    }
}
