package com.nubiz.nutrust.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationSubscriptionId implements Serializable {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_type", nullable = false, length = 50)
    private String eventType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NotificationSubscriptionId)) return false;
        NotificationSubscriptionId that = (NotificationSubscriptionId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(eventType, that.eventType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, eventType);
    }
}
