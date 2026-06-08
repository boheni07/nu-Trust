package com.nubiz.nutrust.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ProjectResponse {

    private Long id;
    private Long companyId;
    private String companyName;
    private Long customerCompanyId;
    private String customerCompanyName;
    private String name;
    private Long ownerId;
    private String ownerName;
    private LocalDate contractDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String statusLabel;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(com.nubiz.nutrust.entity.Project project,
                                       com.nubiz.nutrust.entity.Company company,
                                       String ownerName) {
        return ProjectResponse.builder()
            .id(project.getId())
            .companyId(project.getCompanyId())
            .companyName(company != null ? company.getName() : null)
            .customerCompanyId(project.getCustomerCompanyId())
            .customerCompanyName(null)
            .name(project.getName())
            .ownerId(project.getOwnerId())
            .ownerName(ownerName)
            .contractDate(project.getContractDate())
            .startDate(project.getStartDate())
            .endDate(project.getEndDate())
            .status(project.getStatus())
            .statusLabel(statusToLabel(project.getStatus()))
            .description(project.getDescription())
            .createdAt(project.getCreatedAt())
            .updatedAt(project.getUpdatedAt())
            .build();
    }

    private static String statusToLabel(String status) {
        return switch (status) {
            case "ACTIVE" -> "활성";
            case "COMPLETED" -> "완료";
            case "ON_HOLD" -> "보류";
            case "CANCELLED" -> "취소";
            default -> status;
        };
    }
}
