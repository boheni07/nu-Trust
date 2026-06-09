package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Getter
@Setter
@Builder
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 50)
    private String eventId;

    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;

    @Column(name = "ticket_id")
    private Long ticketId;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "payload", length = 2000)
    private String payload;

    @Column(name = "sent_via", nullable = false, length = 20)
    private String sentVia;

    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "QUEUED";

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
