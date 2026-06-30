package com.coachpad.presentation.rest.controller;

import com.coachpad.infrastructure.service.project.ProjectService;
import com.coachpad.infrastructure.service.project.TacticalDataService;
import com.coachpad.infrastructure.persistance.postgresql.mapper.ProjectEntityMapper;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.UserEntity;
import com.coachpad.presentation.rest.dto.ProjectDTO;
import com.coachpad.presentation.rest.dto.ProjectContentDTO;
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
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ProjectService projectService;
    @MockBean private ProjectEntityMapper projectMapper;
    @MockBean private TacticalDataService tacticalDataService;

    private final UserEntity testUser = UserEntity.builder()
            .id(1L)
            .email("test@test.com")
            .password("pass")
            .build();

    @Test
    void getAllProjects_ShouldReturn200() throws Exception {
        when(projectService.getAllProjectsForUser(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/projects").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectById_ShouldReturn200_WhenFound() throws Exception {
        when(projectService.getProjectByIdForUser("proj-1", 1L))
                .thenReturn(Optional.of(new ProjectEntity()));
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(get("/api/projects/proj-1").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectById_ShouldReturn404_WhenNotFound() throws Exception {
        when(projectService.getProjectByIdForUser("unknown", 1L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/unknown").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveProject_ShouldReturn200() throws Exception {
        when(projectMapper.toEntity(any())).thenReturn(new ProjectEntity());
        when(projectService.saveProjectForUser(any(), eq(1L))).thenReturn(new ProjectEntity());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(post("/api/projects")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"New Project\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateProject_ShouldReturn200() throws Exception {
        when(projectMapper.toEntity(any())).thenReturn(new ProjectEntity());
        when(projectService.updateProjectMetadata(eq("proj-1"), any(), eq(1L)))
                .thenReturn(new ProjectEntity());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(put("/api/projects/proj-1")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteProject_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/projects/proj-1").with(user(testUser)))
                .andExpect(status().isNoContent());
    }

    @Test
    void getByCategory_ShouldReturn200() throws Exception {
        when(projectService.getProjectsByCategoryForUser(any(), eq(1L))).thenReturn(List.of());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(get("/api/projects/category/MACROCYCLE").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getChildren_ShouldReturn200() throws Exception {
        when(projectService.getChildProjectsForUser(eq("parent-1"), any(), eq(1L)))
                .thenReturn(List.of());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(get("/api/projects/parent-1/children?category=MESOCYCLE")
                        .with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void linkProjects_ShouldReturn200() throws Exception {
        when(projectService.linkProjects(eq("parent-1"), anyList(), any()))
                .thenReturn(true);

        mockMvc.perform(post("/api/projects/link?parentId=parent-1&category=MACROCYCLE")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"child-1\",\"child-2\"]"))
                .andExpect(status().isOk());
    }

    @Test
    void linkProjects_ShouldReturn400_WhenFails() throws Exception {
        when(projectService.linkProjects(any(), anyList(), any()))
                .thenReturn(false);

        mockMvc.perform(post("/api/projects/link?parentId=parent-1&category=MACROCYCLE")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[\"child-1\"]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unlinkProject_ShouldReturn200() throws Exception {
        when(projectService.unlinkProject("parent-1", "child-1")).thenReturn(true);

        mockMvc.perform(post("/api/projects/unlink?parentId=parent-1&childId=child-1")
                        .with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void unlinkProject_ShouldReturn400_WhenFails() throws Exception {
        when(projectService.unlinkProject("parent-1", "child-1")).thenReturn(false);

        mockMvc.perform(post("/api/projects/unlink?parentId=parent-1&childId=child-1")
                        .with(user(testUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void toggleArchive_ShouldReturn200() throws Exception {
        when(projectService.toggleArchive("proj-1", 1L)).thenReturn(new ProjectEntity());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(patch("/api/projects/proj-1/archive").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void toggleFavorite_ShouldReturn200() throws Exception {
        when(projectService.toggleFavorite("proj-1", 1L)).thenReturn(new ProjectEntity());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(patch("/api/projects/proj-1/favorite").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void duplicateProject_ShouldReturn200() throws Exception {
        when(projectService.duplicateProject("proj-1", 1L)).thenReturn(new ProjectEntity());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(post("/api/projects/proj-1/duplicate").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getDescendants_ShouldReturn200() throws Exception {
        when(projectService.getDescendantProjects("parent-1")).thenReturn(List.of());
        when(projectMapper.toDTO(any())).thenReturn(new ProjectDTO());

        mockMvc.perform(get("/api/projects/parent-1/descendants").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectContent_ShouldReturn200_WhenFound() throws Exception {
        when(tacticalDataService.getProjectContent("proj-1"))
                .thenReturn(Optional.of(new ProjectContentDTO()));

        mockMvc.perform(get("/api/projects/proj-1/content").with(user(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    void getProjectContent_ShouldReturn404_WhenNotFound() throws Exception {
        when(tacticalDataService.getProjectContent("proj-1"))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/projects/proj-1/content").with(user(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    void saveProjectContent_ShouldReturn200() throws Exception {
        mockMvc.perform(post("/api/projects/proj-1/content")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":\"proj-1\",\"content\":\"data\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void saveProjectContent_ShouldReturn400_WhenIdMismatch() throws Exception {
        mockMvc.perform(post("/api/projects/proj-1/content")
                        .with(user(testUser))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"projectId\":\"wrong-id\",\"content\":\"data\"}"))
                .andExpect(status().isBadRequest());
    }
}