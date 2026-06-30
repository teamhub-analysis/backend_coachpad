package com.coachpad.presentation.rest.controller;

import com.coachpad.infrastructure.security.AuthService;
import com.coachpad.presentation.rest.dto.AuthResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private AuthService authService;

    @Test
    void register_ShouldReturn200() throws Exception {
        when(authService.register(any()))
                .thenReturn(AuthResponse.builder().token("test-token").build());

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void login_ShouldReturn200() throws Exception {
        when(authService.authenticate(any()))
                .thenReturn(AuthResponse.builder().token("login-token").build());

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"a@b.com\",\"password\":\"pass123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("login-token"));
    }

    @Test
    void register_ShouldReturn400_WhenEmailMissing() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"pass123\"}"))
                .andExpect(status().isBadRequest());
    }
}