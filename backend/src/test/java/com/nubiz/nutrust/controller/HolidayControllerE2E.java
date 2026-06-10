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

public class HolidayControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("공휴일 생성 - 성공")
    void create_success() throws Exception {
        String body = """
            {
                "holidayDate": "2024-12-25",
                "holidayName": "크리스마스",
                "holidayType": "NATIONAL"
            }
            """;

        mockMvc.perform(post("/api/v1/system/holidays")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holidayName").value("크리스마스"));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void create_missingFields_returnsBadRequest() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/v1/system/holidays")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("공휴일 목록 조회 - 성공")
    void listAll_success() throws Exception {
        mockMvc.perform(get("/api/v1/system/holidays")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("공휴일 상세 조회 - 성공")
    void getById_success() throws Exception {
        mockMvc.perform(get("/api/v1/system/holidays/{id}", seededHolidayId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("공휴일 수정 - 성공")
    void update_success() throws Exception {
        String body = """
            {
                "holidayDate": "2024-12-25",
                "holidayName": "크리스마스 (수정)",
                "holidayType": "NATIONAL"
            }
            """;

        mockMvc.perform(put("/api/v1/system/holidays/{id}", seededHolidayId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.holidayName").value("크리스마스 (수정)"));
    }

    @Test
    @DisplayName("공휴일 삭제 - 성공")
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/v1/system/holidays/{id}", seededHolidayId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("공휴일 범위 조회 - 성공")
    void findByDateRange_success() throws Exception {
        mockMvc.perform(get("/api/v1/system/holidays/range")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .param("startDate", "2024-01-01")
                .param("endDate", "2024-12-31"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }
} 
