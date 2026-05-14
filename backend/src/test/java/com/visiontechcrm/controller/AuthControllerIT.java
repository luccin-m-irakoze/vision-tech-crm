package com.visiontechcrm.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.visiontechcrm.dto.LoginRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Integration tests for {@link AuthController}.
 *
 * <p>Boots the full Spring ApplicationContext against an in-memory H2 database
 * (configured in {@code application-test.properties}) and drives the
 * {@code /api/auth/login} endpoint through {@link MockMvc}. The {@code DataLoader}
 * runs on context start-up, so the seeded admin account is available to the
 * test — this is exactly the path the examiner uses through the browser.
 *
 * <p>Covers Test Plan §3 (Integration), TC-01 (login OK) and TC-02 (login KO).
 */
@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIT {

    @Autowired private WebApplicationContext context;
    @Autowired private FilterChainProxy springSecurityFilterChain;
    @Autowired private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    private MockMvc mvc() {
        if (mockMvc == null) {
            mockMvc =
                    MockMvcBuilders.webAppContextSetup(context)
                            .addFilter(springSecurityFilterChain)
                            .build();
        }
        return mockMvc;
    }

    @Test
    @DisplayName("POST /api/auth/login with seeded admin credentials returns 200 + JWT")
    void login_validAdmin_returnsJwt() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@visiontech.rw");
        req.setPassword("Admin@123");

        mvc().perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", notNullValue()))
                .andExpect(jsonPath("$.email", is("admin@visiontech.rw")))
                .andExpect(jsonPath("$.role", is("ADMIN")))
                .andExpect(jsonPath("$.fullName", is("System Administrator")))
                .andExpect(jsonPath("$.expiresInMs").isNumber());
    }

    @Test
    @DisplayName("POST /api/auth/login with a wrong password returns 401 Unauthorized")
    void login_wrongPassword_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("admin@visiontech.rw");
        req.setPassword("definitely-wrong");

        mvc().perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("POST /api/auth/login with a missing-user email returns 401 Unauthorized")
    void login_unknownUser_returns401() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setEmail("ghost@visiontech.rw");
        req.setPassword("whatever");

        mvc().perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }
}
