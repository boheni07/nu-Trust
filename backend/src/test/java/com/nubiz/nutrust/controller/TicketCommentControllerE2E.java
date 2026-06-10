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

public class TicketCommentControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("특징 코멘트 생성 - 성공")
    void createComment_success() throws Exception {
        String body = String.format("""
            {
                "ticketId": %d,
                "content": "팀원 코멘트입니다.",
                "parentId": null
            }
            """, (int) seededTicketId);

        mockMvc.perform(post("/api/tickets/{ticketId}/comments", seededTicketId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("팀원 코멘트입니다."));
    }

    @Test
    @DisplayName("대댓글 생성 - 성공")
    void createReply_success() throws Exception {
        String body = String.format("""
            {
                "ticketId": %d,
                "content": "대댓글입니다.",
                "parentId": null
            }
            """, (int) seededTicketId);

        mockMvc.perform(post("/api/tickets/{ticketId}/comments", seededTicketId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("대댓글입니다."));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createComment_missingFields_returnsBadRequest() throws Exception {
        String body = """
            {
                "content": "내용만 있음"
            }
            """;

        mockMvc.perform(post("/api/tickets/{ticketId}/comments", seededTicketId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("특징 코멘트 목록 조회 (루트만) - 성공")
    void getAllRootComments_success() throws Exception {
        mockMvc.perform(get("/api/tickets/{ticketId}/comments", seededTicketId)
                    .header("Authorization", "Bearer " + token)
                    .param("rootOnly", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("모든 코멘트 목록 조회 - 성공")
    void getAllComments_success() throws Exception {
        mockMvc.perform(get("/api/tickets/{ticketId}/comments", seededTicketId)
                    .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}
