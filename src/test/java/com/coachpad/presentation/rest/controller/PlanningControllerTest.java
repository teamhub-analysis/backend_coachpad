package com.coachpad.presentation.rest.controller;

import com.coachpad.infrastructure.service.project.PlanningUseCases;
import com.coachpad.infrastructure.persistance.postgresql.entity.UserEntity;
import com.coachpad.presentation.rest.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlanningControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private PlanningUseCases planningService;

    private final UserEntity testUser = UserEntity.builder()
            .id(1L).email("test@test.com").password("pass").build();

    // ===== MACROCYCLES =====

    @Test
    void getAllMacrocycles_ShouldReturn200() throws Exception {
        when(planningService.getAllMacrocycles(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/macrocycles").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMacrocycleById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getMacrocycleById("mc-1", 1L))
                .thenReturn(Optional.of(new MacrocycleDTO()));

        mockMvc.perform(get("/api/v2/planning/macrocycles/mc-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMacrocycleById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getMacrocycleById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/macrocycles/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMacrocycle_ShouldReturn200() throws Exception {
        when(planningService.saveMacrocycle(any(), eq(1L)))
                .thenReturn(new MacrocycleDTO());

        mockMvc.perform(post("/api/v2/planning/macrocycles")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Season 2025\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMacrocycle_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/macrocycles/mc-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    // ===== MESOCYCLES =====

    @Test
    void getMesocyclesForMacrocycle_ShouldReturn200() throws Exception {
        when(planningService.getMesocyclesForMacrocycle("mc-1", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/mesocycles/macrocycle/mc-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMesocycleById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getMesocycleById("ms-1", 1L))
                .thenReturn(Optional.of(new MesocycleDTO()));

        mockMvc.perform(get("/api/v2/planning/mesocycles/ms-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMesocycleById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getMesocycleById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/mesocycles/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMesocycle_ShouldReturn200() throws Exception {
        when(planningService.saveMesocycle(any(), eq(1L)))
                .thenReturn(new MesocycleDTO());

        mockMvc.perform(post("/api/v2/planning/mesocycles")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Block 1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMesocycle_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/mesocycles/ms-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    // ===== MICROCYCLES =====

    @Test
    void getMicrocyclesForMesocycle_ShouldReturn200() throws Exception {
        when(planningService.getMicrocyclesForMesocycle("ms-1", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/microcycles/mesocycle/ms-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMicrocycleById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getMicrocycleById("mic-1", 1L))
                .thenReturn(Optional.of(new MicrocycleDTO()));

        mockMvc.perform(get("/api/v2/planning/microcycles/mic-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMicrocycleById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getMicrocycleById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/microcycles/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMicrocycle_ShouldReturn200() throws Exception {
        when(planningService.saveMicrocycle(any(), eq(1L)))
                .thenReturn(new MicrocycleDTO());

        mockMvc.perform(post("/api/v2/planning/microcycles")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Week 1\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void generateMicrocycles_ShouldReturn200() throws Exception {
        when(planningService.generateMicrocycles(eq("ms-1"), eq(1L), anyString()))
                .thenReturn(List.of(new MicrocycleDTO()));

        mockMvc.perform(post("/api/v2/planning/microcycles/generate/ms-1")
                        .with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMicrocycle_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/microcycles/mic-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    // ===== SESSIONS =====

    @Test
    void getSessionsForMicrocycle_ShouldReturn200() throws Exception {
        when(planningService.getSessionsForMicrocycle("mic-1", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/sessions/microcycle/mic-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getSessionById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getSessionById("sess-1", 1L))
                .thenReturn(Optional.of(new TrainingSessionDTO()));

        mockMvc.perform(get("/api/v2/planning/sessions/sess-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getSessionById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getSessionById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/sessions/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveSession_ShouldReturn200() throws Exception {
        when(planningService.saveSession(any(), eq(1L)))
                .thenReturn(new TrainingSessionDTO());

        mockMvc.perform(post("/api/v2/planning/sessions")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Morning Session\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteSession_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/sessions/sess-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    // ===== DRILLS =====

    @Test
    void getDrillsForSession_ShouldReturn200() throws Exception {
        when(planningService.getDrillsForSession("sess-1", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/drills/session/sess-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getDrillById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getDrillById("drill-1", 1L))
                .thenReturn(Optional.of(new DrillDTO()));

        mockMvc.perform(get("/api/v2/planning/drills/drill-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getDrillById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getDrillById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/drills/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveDrill_ShouldReturn200() throws Exception {
        when(planningService.saveDrill(any(), eq(1L)))
                .thenReturn(new DrillDTO());

        mockMvc.perform(post("/api/v2/planning/drills")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Possession Drill\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteDrill_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/drills/drill-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    // ===== MATCHES =====

    @Test
    void getMatchesForMicrocycle_ShouldReturn200() throws Exception {
        when(planningService.getMatchesForMicrocycle("mic-1", 1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v2/planning/matches/microcycle/mic-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchById_ShouldReturn200_WhenFound() throws Exception {
        when(planningService.getMatchById("match-1", 1L))
                .thenReturn(Optional.of(new MatchEventDTO()));

        mockMvc.perform(get("/api/v2/planning/matches/match-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getMatchById_ShouldReturn404_WhenNotFound() throws Exception {
        when(planningService.getMatchById("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v2/planning/matches/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveMatch_ShouldReturn200() throws Exception {
        when(planningService.saveMatch(any(), eq(1L)))
                .thenReturn(new MatchEventDTO());

        mockMvc.perform(post("/api/v2/planning/matches")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Match vs Rival\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteMatch_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/v2/planning/matches/match-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }
}