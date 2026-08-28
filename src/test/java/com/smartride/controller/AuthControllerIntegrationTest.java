package com.smartride.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartride.dto.LoginRequest;
import com.smartride.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // ── register ──────────────────────────────────────────

    @Test
    void register_validRequest_returns200() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Test User");
        req.setEmail("testuser_" + System.currentTimeMillis() + "@smartride.com");
        req.setPassword("Test@1234");
        req.setRole("PASSENGER");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk());
    }

    @Test
    void register_duplicateEmail_returns4xx() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setName("Duplicate");
        req.setEmail("duplicate@smartride.com");
        req.setPassword("Test@1234");
        req.setRole("PASSENGER");

        // first registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // duplicate should fail
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().is4xxClientError());
    }

    @Test
    void register_missingFields_returns4xx() throws Exception {
        RegisterRequest req = new RegisterRequest();
        // empty — no email, no password

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().is4xxClientError());
    }

    // ── login ─────────────────────────────────────────────

    @Test
    void login_validCredentials_returnsJwt() throws Exception {
        // register first
        RegisterRequest reg = new RegisterRequest();
        reg.setName("Login Test");
        reg.setEmail("logintest@smartride.com");
        reg.setPassword("Test@1234");
        reg.setRole("PASSENGER");

        mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reg)));

        // now login
        LoginRequest login = new LoginRequest();
        login.setEmail("logintest@smartride.com");
        login.setPassword("Test@1234");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("logintest@smartride.com");
        login.setPassword("WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void login_nonExistentEmail_returns401() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("nobody@smartride.com");
        login.setPassword("Test@1234");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
            .andExpect(status().isUnauthorized());
    }
}