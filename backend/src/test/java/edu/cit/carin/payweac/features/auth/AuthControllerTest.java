package edu.cit.carin.payweac.features.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String REGISTER_URL = "/api/v1/auth/register";
    private static final String LOGIN_URL    = "/api/v1/auth/login";

    // ── Helpers ──────────────────────────────────────────────────────────────

    private Map<String, String> registerPayload(String email) {
        return Map.of(
                "firstName",  "Test",
                "lastName",   "User",
                "email",      email,
                "password",   "password123",
                "confirmPassword", "password123",
                "roomNumber", "TestRoom101"
        );
    }

    private Map<String, String> loginPayload(String email, String password) {
        return Map.of("email", email, "password", password);
    }

    // ── TC-AUTH-001: Register with valid credentials ──────────────────────────

    @Test
    @DisplayName("TC-AUTH-001: Register with valid credentials returns 201 Created")
    void register_withValidData_returns201() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload("valid@test.com"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    // ── TC-AUTH-002: Register with duplicate email ────────────────────────────

    @Test
    @DisplayName("TC-AUTH-002: Register with duplicate email returns 4xx error")
    void register_withDuplicateEmail_returnsError() throws Exception {
        String payload = objectMapper.writeValueAsString(registerPayload("dupe@test.com"));

        // First registration — should succeed
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());

        // Second registration with same email — should fail
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().is4xxClientError());
    }

    // ── TC-AUTH-003: Login with valid credentials ─────────────────────────────

    @Test
    @DisplayName("TC-AUTH-003: Login with valid credentials returns 200 and JWT token")
    void login_withValidCredentials_returns200WithToken() throws Exception {
        // Register first
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload("logintest@test.com"))))
                .andExpect(status().isCreated());

        // Then login
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload("logintest@test.com", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    // ── TC-AUTH-004: Login with wrong password ────────────────────────────────

    @Test
    @DisplayName("TC-AUTH-004: Login with wrong password returns 4xx error")
    void login_withWrongPassword_returnsError() throws Exception {
        // Register first
        mockMvc.perform(post(REGISTER_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload("wrongpass@test.com"))))
                .andExpect(status().isCreated());

        // Try login with wrong password
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload("wrongpass@test.com", "wrongpassword"))))
                .andExpect(status().is4xxClientError());
    }

    // ── TC-AUTH-005: Login with non-existent user ─────────────────────────────

    @Test
    @DisplayName("TC-AUTH-005: Login with non-existent user returns 4xx error")
    void login_withNonExistentUser_returnsError() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginPayload("nobody@test.com", "password123"))))
                .andExpect(status().is4xxClientError());
    }
}
