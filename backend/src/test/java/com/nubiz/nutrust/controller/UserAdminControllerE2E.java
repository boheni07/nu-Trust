package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.security.SecureRandom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserAdminControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("유저 생성 - 성공 (ADMIN)")
    void createUser_success() throws Exception {
        String body = """
            {
                "email": "admin@test.com",
                "password": "password123",
                "name": "어드민 유저",
                "role": "ADMIN"
            }
            """;

        mockMvc.perform(post("/api/admin/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.email").value("admin@test.com"));
    }

    @Test
    @DisplayName("유저 생성 - 성공 (SUPPORT)")
    void createUser_support_success() throws Exception {
        String body = """
            {
                "email": "support@test.com",
                "password": "password123",
                "name": "지원자",
                "phone": "010-1234-5678",
                "companyId": %d
            }
            """;

        mockMvc.perform(post("/api/admin/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(body, seededCompanyId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("support@test.com"));
    }

    @Test
    @DisplayName("이메일 중복 시 403 반환")
    void createUser_duplicateEmail_returnsForbidden() throws Exception {
        String body = """
            {
                "email": "admin@test.com",
                "password": "password123",
                "name": "중복 유저"
            }
            """;

        mockMvc.perform(post("/api/admin/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createUser_missingRequiredFields_returnsBadRequest() throws Exception {
        String body = """
            {
                "email": "test@test.com"
            }
            """;

        mockMvc.perform(post("/api/admin/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("유저 목록 조회 - 성공")
    void listUsers_success() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("이름으로 유저 검색 - 성공")
    void searchUsersByName_success() throws Exception {
        mockMvc.perform(get("/api/admin/users")
                .param("name", "테스트")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 상세 조회 - 성공")
    void getUserById_success() throws Exception {
        Long randomId = new SecureRandom().nextInt(100) + 1L;
        mockMvc.perform(get("/api/admin/users/{id}", randomId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("유저 정보 수정 - 성공")
    void updateUser_success() throws Exception {
        String body = """
            {
                "id": %d,
                "name": "수정된 이름",
                "phone": "010-9876-5432"
            }
            """;

        mockMvc.perform(put("/api/admin/users")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(body, seededUserId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("수정된 이름"));
    }

    @Test
    @DisplayName("유저 삭제 - 성공")
    void deleteUser_success() throws Exception {
        mockMvc.perform(patch("/api/admin/users/" + seededUserId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("유저 상태 업데이트 - 성공")
    void updateUserStatus_success() throws Exception {
        mockMvc.perform(patch("/api/admin/users/{id}/status", 1)
                .param("status", "INACTIVE")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("INACTIVE"));
    }
}
