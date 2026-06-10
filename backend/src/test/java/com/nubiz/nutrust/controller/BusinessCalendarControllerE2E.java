package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class BusinessCalendarControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;
    private long seededBusinessCalendarId;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("비즈니스 캘린더 생성 - 성공")
    void create_success() throws Exception {
        String body = """
            {
                "calendarName": "테스트 캘린더",
                "startTime": "10:00",
                "endTime": "17:00"
            }
            """;

        MvcResult result = mockMvc.perform(post("/api/v1/system/business-calendars")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.calendarName").value("테스트 캘린더"))
            .andReturn();

        seededBusinessCalendarId = Long.parseLong(
            result.getResponse().getContentAsString()
                .replaceAll("[^0-9]", "")
        );
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void create_missingFields_returnsBadRequest() throws Exception {
        String body = "{}";

        mockMvc.perform(post("/api/v1/system/business-calendars")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("비즈니스 캘린더 목록 조회 - 성공")
    void list_all() throws Exception {
        mockMvc.perform(get("/api/v1/system/business-calendars")
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("비즈니스 캘린더 상세 조회 - 성공")
    void getBusinessCalendar_success() throws Exception {
        mockMvc.perform(get("/api/v1/system/business-calendars/{id}", seededBusinessCalendarId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("비즈니스 캘린더 수정 - 성공")
    void update_success() throws Exception {
        String body = """
            {
                "calendarName": "수정된 캘린더",
                "startTime": "10:00",
                "endTime": "17:00"
            }
            """;

        mockMvc.perform(put("/api/v1/system/business-calendars/{id}", seededBusinessCalendarId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.calendarName").value("수정된 캘린더"));
    }

    @Test
    @DisplayName("비즈니스 캘린더 삭제 - 성공")
    void delete_success() throws Exception {
        mockMvc.perform(delete("/api/v1/system/business-calendars/{id}", seededBusinessCalendarId)
                .header("Authorization", "Bearer " + token)
                .header("X-Company-Id", seededCompanyId))
            .andExpect(status().isNoContent());
    }
}
