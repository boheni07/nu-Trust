package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class NoticeUpdateRequest {

    @Size(max = 500)
    private String title;

    private String content;

    private Boolean isPinned;
}
