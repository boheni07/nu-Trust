package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_support_managers", indexes = {
    @Index(name = "idx_psm_assigned_by", columnList = "assigned_by")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectSupportManager {

    @EmbeddedId
    private ProjectSupportManagerId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @MapsId("supportId")
    @JoinColumn(name = "support_id", nullable = false)
    private User support;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String role = "SUPPORT_SUPPORTER";

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;
}
