package com.nubiz.nutrust.repository;

import com.nubiz.nutrust.entity.NotificationSubscription;
import com.nubiz.nutrust.entity.NotificationSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSubscriptionRepository extends JpaRepository<NotificationSubscription, NotificationSubscriptionId> {
    Optional<NotificationSubscription> findByUserIdAndEventType(Long userId, String eventType);
}
