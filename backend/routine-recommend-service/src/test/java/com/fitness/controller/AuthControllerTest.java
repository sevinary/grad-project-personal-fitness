package com.fitness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ──────────────────────────── 회원가입 ────────────────────────────

    @Test
    @DisplayName("회원가입 성공 → 201 Created, 사용자 정보 반환")
    void signup_success() throws Exception {
        Map<String, String> req = Map.of(
                "username", "alice123",
                "password", "securePass1!",
                "email", "alice@example.com"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("회원가입이 완료되었습니다."))
                .andExpect(jsonPath("$.data.username").value("alice123"))
                .andExpect(jsonPath("$.data.email").value("alice@example.com"))
                .andExpect(jsonPath("$.data.userId").isNumber());
    }

    @Test
    @DisplayName("회원가입 실패 → 409 DUPLICATE_USERNAME (중복 아이디)")
    void signup_fail_duplicateUsername() throws Exception {
        Map<String, String> req = Map.of(
                "username", "dupuser",
                "password", "securePass1!",
                "email", "dup@example.com"
        );

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        Map<String, String> dupReq = Map.of(
                "username", "dupuser",
                "password", "anotherPass1!",
                "email", "other@example.com"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dupReq)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("DUPLICATE_USERNAME"));
    }

    @Test
    @DisplayName("회원가입 실패 → 400 INVALID_REQUEST (아이디 2자, 비밀번호 4자, 잘못된 이메일)")
    void signup_fail_validationError() throws Exception {
        Map<String, String> req = Map.of(
                "username", "ab",
                "password", "1234",
                "email", "not-an-email"
        );

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("회원가입 실패 → 400 INVALID_REQUEST (필수 필드 누락)")
    void signup_fail_missingFields() throws Exception {
        Map<String, String> req = Map.of("username", "onlyname");

        mockMvc.perform(post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    // ──────────────────────────── 로그인 ────────────────────────────

    @Test
    @DisplayName("로그인 성공 → 200 OK, JWT 토큰 반환")
    void login_success() throws Exception {
        Map<String, String> signupReq = Map.of(
                "username", "loginuser",
                "password", "password123",
                "email", "login@example.com"
        );
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupReq)));

        Map<String, String> loginReq = Map.of(
                "username", "loginuser",
                "password", "password123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("loginuser"))
                .andExpect(jsonPath("$.data.userId").isNumber());
    }

    @Test
    @DisplayName("로그인 실패 → 401 UNAUTHORIZED (잘못된 비밀번호)")
    void login_fail_wrongPassword() throws Exception {
        Map<String, String> signupReq = Map.of(
                "username", "wrongpwuser",
                "password", "correctPass1!",
                "email", "wp@example.com"
        );
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupReq)));

        Map<String, String> loginReq = Map.of(
                "username", "wrongpwuser",
                "password", "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("로그인 실패 → 401 UNAUTHORIZED (존재하지 않는 아이디)")
    void login_fail_userNotFound() throws Exception {
        Map<String, String> loginReq = Map.of(
                "username", "ghost",
                "password", "password123"
        );

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }
}
