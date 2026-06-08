package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectSupportManagerAssignRequest {

    @NotNull
    private Long projectId;

    @NotNull
    private Long supportId;

    private String role;
}
