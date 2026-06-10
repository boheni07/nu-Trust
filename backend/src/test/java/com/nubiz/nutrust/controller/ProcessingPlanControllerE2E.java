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

public class ProcessingPlanControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("처리 계획 생성 - 성공")
    void createPlan_success() throws Exception {
        String body = String.format("""
            {
                "ticketId": %d,
                "title": "계획 제목",
                "content": "처리 내용입니다."
            }
            """, (int) seededTicketId);

        mockMvc.perform(post("/api/tickets/{ticketId}/plans", seededTicketId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("처리 계획 목록 조회 - 성공")
    void listPlans_success() throws Exception {
        mockMvc.perform(get("/api/tickets/{ticketId}/plans", seededTicketId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("처리 계획 승인 - 성공")
    void approvePlan_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{ticketId}/plans/{planId}/approve", seededTicketId, seededPlanId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("처리 계획 거부 - 성공")
    void rejectPlan_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{ticketId}/plans/{planId}/reject", seededTicketId, seededPlanId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }
}
