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

public class ExtensionRequestControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("연장 요청 생성 - 성공")
    void createExtension_success() throws Exception {
        String body = """
            {
                "userId": %d,
                "reason": "추가 작업 필요",
                "requestedExtendDays": 3
            }
            """.formatted(seededUserId);

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/extensions", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.reason").value("추가 작업 필요"));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createExtension_missingFields_returnsBadRequest() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/extensions", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("연장 요청 목록 조회 - 성공")
    void listExtensions_success() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/{ticketId}/extensions", seededTicketId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("연장 요청 승인 - 성공")
    void approveExtension_success() throws Exception {
        mockMvc.perform(put("/api/v1/tickets/{ticketId}/extensions/{requestId}/approve", seededTicketId, seededExtensionRequestId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("연장 요청 거부 - 성공")
    void rejectExtension_success() throws Exception {
        mockMvc.perform(put("/api/v1/tickets/{ticketId}/extensions/{requestId}/reject", seededTicketId, seededExtensionRequestId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
