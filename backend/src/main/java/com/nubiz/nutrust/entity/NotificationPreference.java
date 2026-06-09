package com.nubiz.nutrust.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_preferences")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "in_app_enabled", nullable = false)
    @Builder.Default
    private Boolean inAppEnabled = true;

    @Column(name = "push_enabled", nullable = false)
    @Builder.Default
    private Boolean pushEnabled = false;

    @Column(name = "email_enabled", nullable = false)
    @Builder.Default
    private Boolean emailEnabled = false;

    @Column(name = "sound_enabled", nullable = false)
    @Builder.Default
    private Boolean soundEnabled = true;

    @Column(name = "badge_enabled", nullable = false)
    @Builder.Default
    private Boolean badgeEnabled = true;

    @Column(name = "quiet_hours_start", length = 5)
    private String quietHoursStart;

    @Column(name = "quiet_hours_end", length = 5)
    private String quietHoursEnd;

    @Column(name = "quiet_hours_mode", length = 10)
    private String quietHoursMode;
}
