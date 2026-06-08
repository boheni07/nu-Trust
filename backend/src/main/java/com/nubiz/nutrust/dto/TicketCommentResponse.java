package com.nubiz.nutrust.dto;

import com.nubiz.nutrust.entity.TicketComment;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record TicketCommentResponse(
    Long id,
    Long ticketId,
    Long writerId,
    String writerName,
    Long parentId,
    String parentWriterName,
    String content,
    List<String> attachedFiles,
    LocalDateTime createdAt
) {
    public static TicketCommentResponse from(TicketComment c) {
        return TicketCommentResponse.builder()
            .id(c.getId())
            .ticketId(c.getTicket().getId())
            .writerId(c.getWriter().getId())
            .writerName(c.getWriter().getName())
            .parentId(c.getParent() != null ? c.getParent().getId() : null)
            .parentWriterName(c.getParent() != null ? c.getParent().getWriter().getName() : null)
            .content(c.getContent())
            .attachedFiles(c.getAttachedFiles())
            .createdAt(c.getCreatedAt())
            .build();
    }
}
