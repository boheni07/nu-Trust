package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class NotificationControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("알림 발행 - 성공")
    void publishNotification_success() throws Exception {
        String body = String.format("""
            {
                "eventType": "TICKET_CREATED",
                "targetUserId": %d,
                "ticketId": %d
            }
            """, (int) seededUserId, (int) seededTicketId);

        mockMvc.perform(post("/api/v1/notifications/publish")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void publishNotification_missingFields_returnsBadRequest() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/v1/notifications/publish")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("나의 알림 목록 조회 - 성공")
    void getMyNotifications_success() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/my")
                .header("Authorization", "Bearer " + token)
                .param("pageNumber", "0")
                .param("pageSize", "20"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("알림 읽음 처리 - 성공")
    void markNotificationAsRead_success() throws Exception {
        String notifUrl = "/api/v1/notifications/" + seededNotificationId + "/read";
        mockMvc.perform(put(notifUrl)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("모든 알림 읽음 처리 - 성공")
    void markAllAsRead_success() throws Exception {
        mockMvc.perform(put("/api/v1/notifications/read-all")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 구독 설정 - 성공")
    void subscribe_success() throws Exception {
        String body = """
            {
                "eventType": "TICKET_CREATED",
                "channel": "PUSH",
                "isActive": true
            }
            """;

        mockMvc.perform(post("/api/v1/notifications/subscriptions")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 설정 조회 - 성공")
    void getPreferences_success() throws Exception {
        mockMvc.perform(get("/api/v1/notifications/preferences")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("알림 설정 업데이트 - 성공")
    void updatePreferences_success() throws Exception {
        String body = """
            {
                "emailEnabled": true,
                "pushEnabled": false,
                "inAppEnabled": true
            }
            """;

        mockMvc.perform(put("/api/v1/notifications/preferences")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk());
    }
}
