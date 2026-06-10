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

public class TicketControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("티켓 생성 - 성공")
    void createTicket_success() throws Exception {
        String body = """
            {
                "title": "로그인 오류",
                "description": "로그인 시 500 에러 발생",
                "projectId": %d,
                "type": "BUG",
                "priority": "HIGH"
            }
            """;

        mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(body, seededProjectId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("로그인 오류"));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createTicket_missingFields_returnsBadRequest() throws Exception {
        String body = """
            {
                "projectId": 1
            }
            """;

        mockMvc.perform(post("/api/tickets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("티켓 상세 조회 - 성공")
    void getTicketById_success() throws Exception {
        mockMvc.perform(get("/api/tickets/{id}", seededTicketId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("티켓 정보 수정 - 성공")
    void updateTicket_success() throws Exception {
        String body = """
            {
                "title": "수정된 티켓",
                "description": "수정된 설명",
                "type": "ENHANCEMENT",
                "priority": "LOW"
            }
            """;

        mockMvc.perform(put("/api/tickets/{id}", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("수정된 티켓"));
    }

    @Test
    @DisplayName("티켓 삭제 - 성공")
    void deleteTicket_success() throws Exception {
        mockMvc.perform(delete("/api/tickets/{id}", seededTicketId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("프로젝트별 티켓 조회 - 성공")
    void getTicketsByProject_success() throws Exception {
        mockMvc.perform(get("/api/tickets/project/{projectId}", seededProjectId)
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("프로젝트 + 상태로 티켓 검색 - 성공")
    void getTicketsByProjectAndStatus_success() throws Exception {
        mockMvc.perform(get("/api/tickets/project/{projectId}/status/{status}", seededProjectId, "RECEIVED")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("프로젝트별 키워드 검색 - 성공")
    void searchTicketsByKeyWord_success() throws Exception {
        mockMvc.perform(get("/api/tickets/project/{projectId}/search", seededProjectId)
                .param("keyword", "오류")
                .param("page", "0")
                .param("size", "10")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    @DisplayName("프로젝트별 상태별 카운트 - 성공")
    void getTicketCountByStatus_success() throws Exception {
        mockMvc.perform(get("/api/tickets/project/{projectId}/status-counts", seededProjectId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지원자 할당 - 성공")
    void assignSupporter_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{id}/assign", seededTicketId)
                .param("supportId", String.valueOf(seededUserId))
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("진행률 업데이트 - 성공")
    void updateProgress_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{id}/progress", 1L)
                .param("progress", "50")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("티켓 완료 - 성공")
    void completeTicket_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{id}/complete", 1L)
                .param("approve", "true")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("지연 처리 - 성공")
    void delayTicket_success() throws Exception {
        mockMvc.perform(patch("/api/tickets/{id}/delayed", 1L)
                .param("reason", "추가 검토 필요")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}
