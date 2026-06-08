package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProjectManagerAssignRequest {

    @NotNull
    private Long projectId;

    @NotNull
    private Long managerId;

    private String role;
}
