package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_event_subscriptions")
@Getter
@Setter
@Builder
public class NotificationSubscription {

    @EmbeddedId
    private NotificationSubscriptionId id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = new NotificationSubscriptionId(userId, eventType);
        }
    }
}
