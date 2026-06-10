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

public class ProjectControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("프로젝트 생성 - 성공")
    void createProject_success() throws Exception {
        String body = """
            {
                "companyId": 1,
                "customerCompanyId": 2,
                "name": "프로젝트 A",
                "ownerId": 1,
                "contractDate": "2024-01-01",
                "startDate": "2024-01-01",
                "endDate": "2024-12-31",
                "status": "ONGOING",
                "description": "테스트 프로젝트 설명"
            }
            """;

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("프로젝트 A"));
    }

    @Test
    @DisplayName("프로젝트 목록 조회 - 성공")
    void listProjects_success() throws Exception {
        mockMvc.perform(get("/api/projects")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트명으로 검색 - 성공")
    void searchProjectsByName_success() throws Exception {
        mockMvc.perform(get("/api/projects")
                .param("keyword", "프로젝트")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트 상세 조회 - 성공")
    void getProjectById_success() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", seededProjectId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("프로젝트 정보 수정 - 성공")
    void updateProject_success() throws Exception {
        String body = """
            {
                "id": %d,
                "name": "수정된 프로젝트",
                "description": "수정된 설명"
            }
            """;

        mockMvc.perform(put("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(body, seededProjectId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("수정된 프로젝트 이름"));
    }

    @Test
    @DisplayName("프로젝트 삭제 - 성공")
    void deleteProject_success() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", seededProjectId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createProject_missingRequiredFields_returnsBadRequest() throws Exception {
        String body = """
            {
                "name": "프로젝트 X"
            }
            """;

        mockMvc.perform(post("/api/projects")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
