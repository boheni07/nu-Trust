package com.nubiz.nutrust.service;

import com.nubiz.nutrust.dto.*;
import com.nubiz.nutrust.entity.NotificationLog;
import com.nubiz.nutrust.entity.NotificationPreference;
import com.nubiz.nutrust.entity.NotificationSubscription;
import com.nubiz.nutrust.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationLogRepository notificationLogRepository;
    private final NotificationPreferenceRepository notificationPreferenceRepository;
    private final NotificationSubscriptionRepository notificationSubscriptionRepository;

    public final String[] EVENT_TYPES = {"TICKET_CREATED", "TICKET_UPDATED", "TICKET_STATUS_CHANGED", 
                                          "TICKET_ASSIGNED", "TICKET_COMMENT", "CHAT_MESSAGE", 
                                          "DEADLINE_UPCOMING", "DEADLINE_PASSED", "EXTENSION_REQUESTED", "SYSTEM_ALERT"};

    @Transactional
    public void publish(String eventType, Long targetUserId, Long ticketId, Long projectId, String payload) {
        var subscription = notificationSubscriptionRepository.findByUserIdAndEventType(targetUserId, eventType);
        if (subscription.isEmpty() || !subscription.get().getIsActive()) {
            return;
        }

        Optional<NotificationPreference> prefs = notificationPreferenceRepository.findByUserId(targetUserId);
        boolean inAppEnabled = prefs.map(NotificationPreference::getInAppEnabled).orElse(true);
        
        NotificationLog log = NotificationLog.builder()
            .eventId(eventType)
            .targetUserId(targetUserId)
            .ticketId(ticketId)
            .projectId(projectId)
            .payload(payload)
            .sentVia("IN_APP")
            .status("QUEUED")
            .createdAt(LocalDateTime.now())
            .build();
        
        notificationLogRepository.save(log);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        notificationLogRepository.findById(notificationId).ifPresent(log -> {
            log.setStatus("READ");
            log.setReadAt(LocalDateTime.now());
        });
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<NotificationLog> unread = notificationLogRepository.findByTargetUserIdAndStatusInOrderByCreatedAtDesc(
            userId, List.of("QUEUED", "SENT")
        );
        for (NotificationLog log : unread) {
            log.setStatus("READ");
            log.setReadAt(LocalDateTime.now());
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(Long userId, int pageNumber, int pageSize) {
        List<NotificationLog> logs = notificationLogRepository.findByTargetUserIdOrderByCreatedAtDesc(
            userId, PageRequest.of(pageNumber, pageSize)
        ).getContent();
        return logs.stream()
            .map(log -> new NotificationResponse(
                log.getId(),
                log.getEventId(),
                log.getPayload(),
                log.getSentVia(),
                log.getCreatedAt(),
                "READ".equals(log.getStatus())
            ))
            .collect(Collectors.toList());
    }

    @Transactional
    public NotificationPreference updatePreferences(Long userId, NotificationPreferencesRequest request) {
        NotificationPreference prefs = notificationPreferenceRepository.findByUserId(userId)
            .orElseGet(() -> {
                NotificationPreference newPrefs = new NotificationPreference();
                newPrefs.setUserId(userId);
                return newPrefs;
            });
        
        if (request.inAppEnabled() != null) prefs.setInAppEnabled(request.inAppEnabled());
        if (request.pushEnabled() != null) prefs.setPushEnabled(request.pushEnabled());
        if (request.emailEnabled() != null) prefs.setEmailEnabled(request.emailEnabled());
        if (request.soundEnabled() != null) prefs.setSoundEnabled(request.soundEnabled());
        if (request.badgeEnabled() != null) prefs.setBadgeEnabled(request.badgeEnabled());
        if (request.quietHoursStart() != null) prefs.setQuietHoursStart(request.quietHoursStart());
        if (request.quietHoursEnd() != null) prefs.setQuietHoursEnd(request.quietHoursEnd());
        if (request.quietHoursMode() != null) prefs.setQuietHoursMode(request.quietHoursMode());
        
        return notificationPreferenceRepository.save(prefs);
    }

    @Transactional(readOnly = true)
    public Optional<NotificationPreference> getPreferences(Long userId) {
        return notificationPreferenceRepository.findByUserId(userId);
    }

    @Transactional
    public void subscribe(Long userId, String eventType, String channel, boolean isActive) {
        NotificationSubscription subscription = NotificationSubscription.builder()
            .userId(userId)
            .eventType(eventType)
            .channel(channel)
            .isActive(isActive)
            .createdAt(LocalDateTime.now())
            .build();
        
        notificationSubscriptionRepository.save(subscription);
    }
}
