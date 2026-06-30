package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.usecase.PlayerUseCase;
import com.coachpad.infrastructure.service.storage.FileStorageService;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class PlayerControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private PlayerUseCase playerUseCase;
    @MockBean private PlayerDTOMapper playerDTOMapper;
    @MockBean private FileStorageService fileStorageService;

    @Test
    void getPlayerById_ShouldReturn200_WhenFound() throws Exception {
        when(playerUseCase.getPlayerById(1L)).thenReturn(Optional.of(new PlayerModel()));
        when(playerDTOMapper.toDTO(any())).thenReturn(new PlayerDTO());

        mockMvc.perform(get("/api/players/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPlayerById_ShouldReturn404_WhenNotFound() throws Exception {
        when(playerUseCase.getPlayerById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/players/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPlayersByTeamId_ShouldReturn200() throws Exception {
        when(playerUseCase.getPlayersByTeamId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/players/team/1"))
                .andExpect(status().isOk());
    }

    @Test
    void updatePlayer_ShouldReturn200() throws Exception {
        when(playerDTOMapper.toModel(any())).thenReturn(new PlayerModel());
        when(playerUseCase.updatePlayer(eq(1L), any())).thenReturn(new PlayerModel());
        when(playerDTOMapper.toDTO(any())).thenReturn(new PlayerDTO());

        mockMvc.perform(put("/api/players/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"John\",\"lastName\":\"Doe\",\"number\":10,\"mainPosition\":\"ST\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deletePlayer_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/players/1"))
                .andExpect(status().isNoContent());
    }
}
