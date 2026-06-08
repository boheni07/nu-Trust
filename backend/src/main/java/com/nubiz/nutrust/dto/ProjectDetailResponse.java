package com.nubiz.nutrust.dto;

import com.nubiz.nutrust.entity.*;
import com.nubiz.nutrust.repository.UserRepository;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter
@Builder
public class ProjectDetailResponse {
    private Long id;
    private String name;
    private String company;
    private String companyCode;
    private String customerCompany;
    private String customerCompanyCode;
    private String owner;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate contractDate;
    private String status;
    private String statusLabel;
    private List<ManagerResponse> managers;
    private List<SupportResponse> supports;

    @Getter
    @Builder
    public static class ManagerResponse {
        private Long managerId;
        private String name;
        private String email;
        private String role;
    }

    @Getter
    @Builder
    public static class SupportResponse {
        private Long supportId;
        private String name;
        private String email;
        private String role;
    }

    public static ProjectDetailResponse from(
            Project project,
            Company company,
            Company customerCompany,
            User owner
    ) {
        return ProjectDetailResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .company(company != null ? company.getName() : null)
                .companyCode(company != null ? company.getBusinessNumber() : null)
                .customerCompany(customerCompany != null ? customerCompany.getName() : null)
                .customerCompanyCode(customerCompany != null ? customerCompany.getBusinessNumber() : null)
                .owner(owner != null ? owner.getName() : null)
                .description(project.getDescription())
                .startDate(project.getStartDate())
                .endDate(project.getEndDate())
                .contractDate(project.getContractDate())
                .status(project.getStatus())
                .statusLabel(toStatusLabel(project.getStatus()))
                .managers(new ArrayList<>())
                .supports(new ArrayList<>())
                .build();
    }

    private static String toStatusLabel(String status) {
        if (status == null) return "";
        return switch (status) {
            case "ACTIVE" -> "활성";
            case "COMPLETED" -> "완료";
            case "ON_HOLD" -> "보류";
            case "CANCELLED" -> "취소";
            default -> status;
        };
    }
}
