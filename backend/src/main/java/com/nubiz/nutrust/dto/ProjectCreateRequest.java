package com.nubiz.nutrust.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ProjectCreateRequest {

    @NotNull
    private Long companyId;

    private Long customerCompanyId;

    @NotBlank
    private String name;

    @NotNull
    private Long ownerId;

    private LocalDate contractDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private String status;
    private String description;
}
