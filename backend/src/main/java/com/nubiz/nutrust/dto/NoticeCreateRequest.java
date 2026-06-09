package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor @Builder
public class NoticeCreateRequest {

    @NotBlank
    @Size(max = 500)
    private String title;

    @NotBlank
    private String content;

    private Boolean isPinned;
}
