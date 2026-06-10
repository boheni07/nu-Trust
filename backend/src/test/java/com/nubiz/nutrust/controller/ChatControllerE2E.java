package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ChatControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private String token;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        token = loginAsAdmin();
    }

    @Test
    @DisplayName("채팅 메시지 전송 - 성공")
    void sendMessage_success() throws Exception {
        String body = """
            {
                "ticketId": 1,
                "content": "테스트 메시지입니다.",
                "imageUrl": []
            }
            """;

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/chat/messages", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content").value("테스트 메시지입니다."));
    }

    @Test
    @DisplayName("필수 필드 누락 시 400 반환")
    void sendMessage_missingContent_returnsBadRequest() throws Exception {
        String body = """
            {
                "content": ""
            }
            """;

        mockMvc.perform(post("/api/v1/tickets/{ticketId}/chat/messages", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메시지 목록 조회 - 성공")
    void getMessages_success() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/{ticketId}/chat/messages", seededTicketId)
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("특정 시간 이후 메시지 조회 - 성공")
    void getMessagesSince_success() throws Exception {
        mockMvc.perform(get("/api/v1/tickets/{ticketId}/chat/messages", seededTicketId)
                .param("since", "2024-01-01T00:00:00")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("메시지 수정 - 성공")
    void editMessage_success() throws Exception {
        String sendBody = """
            {
                "ticketId": 1,
                "content": "원본 메시지입니다.",
                "imageUrl": []
            }
            """;

        MvcResult sendResult = mockMvc.perform(post("/api/v1/tickets/{ticketId}/chat/messages", seededTicketId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(sendBody))
            .andExpect(status().isOk())
            .andReturn();

        Long messageId = Long.parseLong(
            objectMapper.readTree(sendResult.getResponse().getContentAsString()).get("id").asText());

        String editBody = """
            {
                "ticketId": 1,
                "content": "수정된 메시지입니다.",
                "imageUrl": []
            }
            """;

        MvcResult putResult = mockMvc.perform(put("/api/v1/tickets/{ticketId}/chat/messages/{messageId}", seededTicketId, messageId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(editBody))
            .andReturn();

        String putResponse = putResult.getResponse().getContentAsString();
        System.out.println("PUT status=" + putResult.getResponse().getStatus() + " body=" + putResponse);

        if (putResult.getResponse().getStatus() == 200) {
            mockMvc.perform(put("/api/v1/tickets/{ticketId}/chat/messages/{messageId}", seededTicketId, messageId)
                    .header("Authorization", "Bearer " + token)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(editBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 메시지입니다."));
        } else {
            // Fail with diagnostic info
            org.junit.jupiter.api.Assertions.fail("PUT failed with status=" + putResult.getResponse().getStatus() + " body=" + putResponse);
        }
    }
}
