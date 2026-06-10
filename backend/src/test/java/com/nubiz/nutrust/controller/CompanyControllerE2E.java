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

public class CompanyControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("회사 생성 - 성공")
    void createCompany_success() throws Exception {
        String body = """
            {
                "name": "테스트 회사",
                "businessNumber": "123-45-67890",
                "address": "서울시 강남구"
            }
            """;

        mockMvc.perform(post("/api/admin/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("테스트 회사"));
    }

    @Test
    @DisplayName("회사 목록 조회 - 성공")
    void listCompanies_success() throws Exception {
        String createBody = """
            {
                "name": "목록 테스트 회사",
                "businessNumber": "987-65-43210"
            }
            """;
        mockMvc.perform(post("/api/admin/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated());

        mockMvc.perform(get("/api/admin/companies")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").isNotEmpty());
    }

    @Test
    @DisplayName("회사명으로 검색 - 성공")
    void searchCompaniesByName_success() throws Exception {
        mockMvc.perform(get("/api/admin/companies")
                .param("name", "테스트")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회사 상세 조회 - 성공")
    void getCompanyById_success() throws Exception {
        String createBody = """
            {
                "name": "상세 조회 테스트",
                "businessNumber": "111-22-33333"
            }
            """;
        String location = mockMvc.perform(post("/api/admin/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(createBody))
            .andExpect(status().isCreated())
            .andReturn().getRequest().getRequestURI().replace("/api/admin/companies", "") + "/{id}";

        mockMvc.perform(get("/api/admin/companies/{id}", seededCompanyId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회사 업데이트 - 성공")
    void updateCompany_success() throws Exception {
        String updateBody = """
            {
                "id": %d,
                "name": "업데이트된 회사",
                "address": "서울시 송파구"
            }
            """;

        mockMvc.perform(put("/api/admin/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(String.format(updateBody, seededCompanyId)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("업데이트된 회사"));
    }

    @Test
    @DisplayName("회사 삭제 - 성공")
    void deleteCompany_success() throws Exception {
        mockMvc.perform(delete("/api/admin/companies/{id}", seededCompanyId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void createCompany_missingName_returnsBadRequest() throws Exception {
        String body = """
            {
                "businessNumber": "123-45-67890"
            }
            """;

        mockMvc.perform(post("/api/admin/companies")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }
}
