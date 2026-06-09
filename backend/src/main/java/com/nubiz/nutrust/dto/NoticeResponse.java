package com.nubiz.nutrust.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter @AllArgsConstructor @NoArgsConstructor @Builder
public class NoticeResponse {

    private Long id;
    private Long companyId;
    private String title;
    private String content;
    private Boolean isPinned;
    private String status;
    private Long authorId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
