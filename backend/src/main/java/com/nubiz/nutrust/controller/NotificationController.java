package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.dto.NotificationPublishRequest;
import com.nubiz.nutrust.dto.NotificationPreferencesRequest;
import com.nubiz.nutrust.dto.NotificationResponse;
import com.nubiz.nutrust.dto.NotificationSubscriptionRequest;
import com.nubiz.nutrust.entity.NotificationPreference;
import com.nubiz.nutrust.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/publish")
    public ResponseEntity<Void> publish(@Valid @RequestBody NotificationPublishRequest request) {
        notificationService.publish(
            request.eventType(),
            request.targetUserId(),
            request.ticketId(),
            request.projectId(),
            request.payload()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<NotificationResponse>> getMyNotifications(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize) {
        List<NotificationResponse> notifications = notificationService.getNotifications(
            getCurrentUserId(), pageNumber, pageSize
        );
        return ResponseEntity.ok(notifications);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        notificationService.markAllAsRead(getCurrentUserId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/subscriptions")
    public ResponseEntity<Void> subscribe(@Valid @RequestBody NotificationSubscriptionRequest request) {
        notificationService.subscribe(
            getCurrentUserId(),
            request.eventType(),
            request.channel(),
            request.isActive()
        );
        return ResponseEntity.ok().build();
    }

    @GetMapping("/preferences")
    public ResponseEntity<NotificationPreference> getPreferences() {
        Optional<NotificationPreference> prefs = notificationService.getPreferences(getCurrentUserId());
        return prefs.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/preferences")
    public ResponseEntity<NotificationPreference> updatePreferences(@Valid @RequestBody NotificationPreferencesRequest request) {
        NotificationPreference prefs = notificationService.updatePreferences(getCurrentUserId(), request);
        return ResponseEntity.ok(prefs);
    }

    private Long getCurrentUserId() {
        return 1L;
    }
}
