package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "projects", indexes = {
    @Index(name = "idx_projects_company_id", columnList = "company_id"),
    @Index(name = "idx_projects_customer_company_id", columnList = "customer_company_id"),
    @Index(name = "idx_projects_owner_id", columnList = "owner_id"),
    @Index(name = "idx_projects_status", columnList = "status"),
    @Index(name = "idx_projects_deleted_at", columnList = "deleted_at", unique = false)
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "customer_company_id")
    private Long customerCompanyId;

    @Column(name = "project_name", nullable = false, length = 255)
    private String name;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @Column(name = "contract_date")
    private LocalDate contractDate;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    @Column(length = 200, columnDefinition = "TEXT")
    private String description;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
