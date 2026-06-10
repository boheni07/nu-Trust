package com.nubiz.nutrust.controller;

import com.nubiz.nutrust.BaseE2E;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerE2E extends BaseE2E {

    @Autowired
    private MockMvc mockMvc;

    private static final String PASSWORD = "password123";
    private static final String NAME = "E2E Test";
    private static final Long COMPANY_ID = 1L;

    private String buildRegisterBody(String email, String password, String name, Long companyId) {
        return """
                {
                    "email": "%s",
                    "password": "%s",
                    "name": "%s",
                    "companyId": %d
                }
                """.formatted(email, password, name, companyId);
    }

    private String buildLoginBody(String email, String password) {
        return """
                {
                    "email": "%s",
                    "password": "%s"
                }
                """.formatted(email, password);
    }

    private String buildRefreshBody(String refreshToken) {
        return """
                {
                    "refreshToken": "%s"
                }
                """.formatted(refreshToken);
    }

    @Test
    void testRegister_success() throws Exception {
        String uniqueEmail = "e2e_register_test" + System.currentTimeMillis() + "@example.com";
        String body = buildRegisterBody(uniqueEmail, PASSWORD, NAME, COMPANY_ID);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.id").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(uniqueEmail))
                .andExpect(jsonPath("$.user.name").value(NAME))
                .andExpect(jsonPath("$.user.companyId").isNumber())
                .andReturn();
    }

    @Test
    void testRegister_duplicateEmail() throws Exception {
        String uniqueEmail = "e2e_dup_test" + System.currentTimeMillis() + "@example.com";
        String body = buildRegisterBody(uniqueEmail, PASSWORD, NAME, COMPANY_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isCreated());

        // Second registration with the same email -> conflict
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void testLogin_success() throws Exception {
        String uniqueEmail = "e2e_login_test" + System.currentTimeMillis() + "@example.com";
        String registerBody = buildRegisterBody(uniqueEmail, PASSWORD, NAME, COMPANY_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andDo(print())
                .andExpect(status().isCreated());

        // Login with valid credentials
        String loginBody = buildLoginBody(uniqueEmail, PASSWORD);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.user.id").isNotEmpty())
                .andExpect(jsonPath("$.user.email").value(uniqueEmail));
    }

    @Test
    void testLogin_invalidCredentials() throws Exception {
        String uniqueEmail = "e2e_invalid_test" + System.currentTimeMillis() + "@example.com";
        String registerBody = buildRegisterBody(uniqueEmail, PASSWORD, NAME, COMPANY_ID);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andDo(print())
                .andExpect(status().isCreated());

        // Login with wrong password
        String loginBody = buildLoginBody(uniqueEmail, "WrongPassword123");
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testRefreshToken_success() throws Exception {
        String uniqueEmail = "e2e_refresh_test" + System.currentTimeMillis() + "@example.com";
        String registerBody = buildRegisterBody(uniqueEmail, PASSWORD, NAME, COMPANY_ID);
        String registerResponse = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract refreshToken from the JSON response
        String refreshToken = extractRefreshToken(registerResponse);

        // Step 2: Use the refreshToken to get a new accessToken
        String refreshBody = buildRefreshBody(refreshToken);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    /**
     * Minimal JSON extractor — pulls the refreshToken value without any 3rd-party lib.
     */
    private String extractRefreshToken(String json) {
        int start = json.indexOf("\"refreshToken\":\"") + "\"refreshToken\":\"".length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}
