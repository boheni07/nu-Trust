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

public class NoticeControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("공지사항 생성 - 성공")
    void createNotice_success() throws Exception {
        String body = """
            {
                "title": "테스트 공지사항",
                "content": "공지사항 내용입니다.",
                "isPinned": false
            }
            """;

        mockMvc.perform(post("/api/v1/notices")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("테스트 공지사항"));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createNotice_missingFields_returnsBadRequest() throws Exception {
        String body = """
            {
                "content": "내용만 있음"
            }
            """;

        mockMvc.perform(post("/api/v1/notices")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공지사항 목록 조회 - 성공")
    void listNotices_success() throws Exception {
        mockMvc.perform(get("/api/v1/notices")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("공지사항 상세 조회 - 성공")
    void getNoticeById_success() throws Exception {
        mockMvc.perform(get("/api/v1/notices/{id}", 1L)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공지사항 수정 - 성공")
    void updateNotice_success() throws Exception {
        String body = """
            {
                "title": "수정된 공지사항 제목",
                "content": "수정된 내용입니다.",
                "isPinned": true
            }
            """;

        mockMvc.perform(put("/api/v1/notices/{id}", 1L)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("수정된 공지사항 제목"));
    }

    @Test
    @DisplayName("공지사항 삭제 - 성공")
    void deleteNotice_success() throws Exception {
        mockMvc.perform(delete("/api/v1/notices/{id}", 1L)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isNoContent());
    }
}
