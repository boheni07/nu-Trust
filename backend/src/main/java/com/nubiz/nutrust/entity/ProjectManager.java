package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "project_managers", indexes = {
    @Index(name = "idx_pm_assigned_by", columnList = "assigned_by")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProjectManager {

    @EmbeddedId
    private ProjectManagerId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @MapsId("managerId")
    @JoinColumn(name = "manager_id", nullable = false)
    private User manager;

    @Column(nullable = false, length = 50)
    @Builder.Default
    private String role = "PROJECT_ADMIN";

    @CreationTimestamp
    @Column(name = "assigned_at", updatable = false)
    private LocalDateTime assignedAt;

    @ManyToOne
    @JoinColumn(name = "assigned_by", nullable = false)
    private User assignedBy;
}
