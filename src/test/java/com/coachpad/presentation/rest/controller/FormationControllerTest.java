package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.model.FormationModel;
import com.coachpad.domain.usecase.FormationUseCase;
import com.coachpad.presentation.rest.dto.FormationDTO;
import com.coachpad.presentation.rest.mapper.FormationDTOMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
class FormationControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private FormationUseCase formationUseCase;
    @MockBean private FormationDTOMapper formationDTOMapper;

    @Test
    void getAllFormations_ShouldReturn200() throws Exception {
        when(formationUseCase.getAllFormations()).thenReturn(List.of());

        mockMvc.perform(get("/api/formations"))
                .andExpect(status().isOk());
    }

    @Test
    void getFormationById_ShouldReturn200_WhenFound() throws Exception {
        when(formationUseCase.getFormationById(1L)).thenReturn(new FormationModel());
        when(formationDTOMapper.toDTO(any())).thenReturn(new FormationDTO());

        mockMvc.perform(get("/api/formations/1"))
                .andExpect(status().isOk());
    }

    @Test
    void createFormations_ShouldReturn200() throws Exception {
        FormationDTO dto = FormationDTO.builder().formationFormat("4-3-3").valid(true).build();
        when(formationDTOMapper.toModel(any())).thenReturn(new FormationModel());
        when(formationUseCase.createFormation(any())).thenReturn(List.of(new FormationModel()));
        when(formationDTOMapper.toDTO(any())).thenReturn(dto);

        mockMvc.perform(post("/api/formations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"formationFormat\":\"4-3-3\"}]"))
                .andExpect(status().isOk());
    }

    @Test
    void updateFormation_ShouldReturn200() throws Exception {
        when(formationDTOMapper.toModel(any())).thenReturn(new FormationModel());
        when(formationUseCase.updateFormation(eq(1L), any())).thenReturn(new FormationModel());
        when(formationDTOMapper.toDTO(any())).thenReturn(new FormationDTO());

        mockMvc.perform(put("/api/formations/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"formationFormat\":\"4-4-2\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteFormation_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/formations/1"))
                .andExpect(status().isNoContent());
    }
}