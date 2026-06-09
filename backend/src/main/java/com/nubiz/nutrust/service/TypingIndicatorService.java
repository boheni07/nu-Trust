package com.nubiz.nutrust.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

@Service
public class TypingIndicatorService {
    private static final long AUTOMATIC_REMOVE_DELAY_SECONDS = 5L;
    private final ConcurrentHashMap<String, LocalDateTime> typingEvents = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public void startTyping(String key, LocalDateTime timestamp) {
        typingEvents.put(key, timestamp);
        scheduler.schedule(() -> {
            typingEvents.remove(key);
        }, AUTOMATIC_REMOVE_DELAY_SECONDS, TimeUnit.SECONDS);
    }

    public Map<String, LocalDateTime> getTypingEvents() {
        return Map.copyOf(typingEvents);
    }

    public void removeTyping(String key) {
        typingEvents.remove(key);
    }

    public boolean isTyping(String key) {
        return hasActiveTypingKey(typingEvents.keySet());
    }

    private boolean hasActiveTypingKey(Set<String> keys) {
        for (String k : keys) {
            LocalDateTime lastTyping = typingEvents.get(k);
            if (lastTyping != null && lastTyping.isAfter(LocalDateTime.now().minusSeconds(AUTOMATIC_REMOVE_DELAY_SECONDS - 1))) {
                return true;
            }
        }
        return false;
    }
}
