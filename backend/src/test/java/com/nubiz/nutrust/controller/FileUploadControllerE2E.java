package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FileUploadControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("파일 업로드 - 성공")
    void uploadFile_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "test.txt",
            "text/plain",
            "test content".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/files/upload")
                .file(file)
                .header("Authorization", "Bearer " + loginWithDefault()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.url").exists());
    }

    @Test
    @DisplayName("빈 파일 업로드 시 400 반환")
    void uploadEmptyFile_returnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file",
            "",
            "text/plain",
            new byte[0]
        );

        mockMvc.perform(multipart("/api/v1/files/upload")
                .file(file)
                .header("Authorization", "Bearer " + loginWithDefault()))
            .andExpect(status().isBadRequest());
    }
}
