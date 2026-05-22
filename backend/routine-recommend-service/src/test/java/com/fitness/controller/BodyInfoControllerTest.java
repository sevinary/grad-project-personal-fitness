package com.fitness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
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
class BodyInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        Map<String, String> signupReq = Map.of(
                "username", "bodyuser",
                "password", "password123",
                "email", "body@example.com"
        );
        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupReq)));

        Map<String, String> loginReq = Map.of(
                "username", "bodyuser",
                "password", "password123"
        );
        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andReturn().getResponse().getContentAsString();

        jwtToken = objectMapper.readTree(response)
                .path("data").path("accessToken").asText();
    }

    // ──────────────────────────── 신체 정보 등록 ────────────────────────────

    @Test
    @DisplayName("신체 정보 등록 성공 (인바디 포함) → 201 Created, hasInbody=true")
    void saveBodyInfo_success_withInbody() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 168.0,
                "weight", 62.5,
                "bodyFatRate", 24.3,
                "muscleMass", 27.1,
                "goal", "DIET",
                "weeklyGoalCount", 4
        );

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.bodyInfoId").isNumber())
                .andExpect(jsonPath("$.data.height").value(168.0))
                .andExpect(jsonPath("$.data.weight").value(62.5))
                .andExpect(jsonPath("$.data.bodyFatRate").value(24.3))
                .andExpect(jsonPath("$.data.muscleMass").value(27.1))
                .andExpect(jsonPath("$.data.goal").value("DIET"))
                .andExpect(jsonPath("$.data.weeklyGoalCount").value(4))
                .andExpect(jsonPath("$.data.hasInbody").value(true));
    }

    @Test
    @DisplayName("신체 정보 등록 성공 (인바디 없음) → 201 Created, hasInbody=false")
    void saveBodyInfo_success_withoutInbody() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 175.0,
                "weight", 70.0,
                "goal", "MUSCLE",
                "weeklyGoalCount", 5
        );

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.goal").value("MUSCLE"))
                .andExpect(jsonPath("$.data.hasInbody").value(false));
    }

    @Test
    @DisplayName("신체 정보 등록 실패 → 401 Unauthorized (JWT 토큰 없음)")
    void saveBodyInfo_fail_noToken() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 168.0,
                "weight", 62.5,
                "goal", "DIET",
                "weeklyGoalCount", 3
        );

        mockMvc.perform(post("/body-info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("신체 정보 등록 실패 → 401 Unauthorized (유효하지 않은 토큰)")
    void saveBodyInfo_fail_invalidToken() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 168.0,
                "weight", 62.5,
                "goal", "DIET",
                "weeklyGoalCount", 3
        );

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer invalid.token.here")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("신체 정보 등록 실패 → 409 BODY_INFO_EXISTS (중복 등록)")
    void saveBodyInfo_fail_duplicate() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 168.0,
                "weight", 62.5,
                "goal", "DIET",
                "weeklyGoalCount", 3
        );

        mockMvc.perform(post("/body-info")
                .header("Authorization", "Bearer " + jwtToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("BODY_INFO_EXISTS"));
    }

    @Test
    @DisplayName("신체 정보 등록 실패 → 400 INVALID_REQUEST (필수 필드 누락)")
    void saveBodyInfo_fail_missingRequiredFields() throws Exception {
        Map<String, Object> req = Map.of(
                "height", 168.0
        );

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    @DisplayName("신체 정보 등록 실패 → 400 INVALID_REQUEST (잘못된 goal 값)")
    void saveBodyInfo_fail_invalidGoal() throws Exception {
        String body = """
                {
                    "height": 168.0,
                    "weight": 62.5,
                    "goal": "INVALID_GOAL",
                    "weeklyGoalCount": 3
                }
                """;

        mockMvc.perform(post("/body-info")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }
}
