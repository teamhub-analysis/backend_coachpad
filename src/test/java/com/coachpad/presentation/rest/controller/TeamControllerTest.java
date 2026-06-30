package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.TeamUseCase;
import com.coachpad.infrastructure.service.storage.FileStorageService;
import com.coachpad.infrastructure.service.dataimport.ExcelImportService;
import com.coachpad.infrastructure.service.dataimport.CsvImportService;
import com.coachpad.presentation.rest.mapper.TeamDTOMapper;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
import com.coachpad.presentation.rest.dto.TeamDTO;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.domain.model.TeamModel;
import com.coachpad.domain.model.PlayerModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TeamControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private TeamUseCase teamUseCase;
    @MockBean private TeamDTOMapper teamDTOMapper;
    @MockBean private PlayerDTOMapper playerDTOMapper;
    @MockBean private FileStorageService fileStorageService;
    @MockBean private ExcelImportService excelImportService;
    @MockBean private CsvImportService csvImportService;

    @Test
    @WithMockUser
    void getAllTeams_ShouldReturn200() throws Exception {
        when(teamUseCase.getAllTeams()).thenReturn(List.of());

        mockMvc.perform(get("/api/teams"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTeamById_ShouldReturn200_WhenFound() throws Exception {
        when(teamUseCase.getTeamById(1L)).thenReturn(Optional.of(new TeamModel()));
        when(teamDTOMapper.toDTO(any())).thenReturn(new TeamDTO());

        mockMvc.perform(get("/api/teams/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void getTeamById_ShouldReturn404_WhenNotFound() throws Exception {
        when(teamUseCase.getTeamById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/teams/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser
    void updateTeam_ShouldReturn200() throws Exception {
        when(teamDTOMapper.toModel(any())).thenReturn(new TeamModel());
        when(teamUseCase.updateTeam(anyLong(), any())).thenReturn(new TeamModel());
        when(teamDTOMapper.toDTO(any())).thenReturn(new TeamDTO());

        mockMvc.perform(put("/api/teams/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser
    void addPlayerToTeam_ShouldReturn201() throws Exception {
        when(playerDTOMapper.toModel(any())).thenReturn(new PlayerModel());
        when(teamUseCase.addPlayerToTeam(anyLong(), any())).thenReturn(new PlayerModel());
        when(playerDTOMapper.toDTO(any())).thenReturn(new PlayerDTO());

        mockMvc.perform(post("/api/teams/1/players")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"number\":10,\"mainPosition\":\"ST\"}"))
                .andExpect(status().isCreated());
    }
}
