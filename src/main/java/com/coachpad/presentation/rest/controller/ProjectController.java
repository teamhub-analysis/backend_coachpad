package com.coachpad.presentation.rest.controller;

import com.coachpad.presentation.rest.dto.ProjectContentDTO;
import com.coachpad.presentation.rest.dto.ProjectDTO;
import com.coachpad.infrastructure.persistance.postgresql.mapper.ProjectEntityMapper;
import com.coachpad.domain.model.enums.ProjectCategory;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.UserEntity;
import com.coachpad.infrastructure.service.project.ProjectService;
import com.coachpad.infrastructure.service.project.TacticalDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectEntityMapper projectMapper;
    private final TacticalDataService tacticalDataService;

    @GetMapping
    public List<ProjectDTO> getAllProjects(@AuthenticationPrincipal UserEntity user) {
        return projectService.getAllProjectsForUser(user.getId()).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<ProjectContentDTO> getProjectContent(@PathVariable String id) {
        return tacticalDataService.getProjectContent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/content")
    public ResponseEntity<Void> saveProjectContent(
            @PathVariable String id,
            @RequestBody ProjectContentDTO content) {
        if (!id.equals(content.getProjectId()))
            return ResponseEntity.badRequest().build();
        tacticalDataService.saveProjectContent(content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/category/{category}")
    public List<ProjectDTO> getByCategory(
            @PathVariable ProjectCategory category,
            @AuthenticationPrincipal UserEntity user) {
        return projectService.getProjectsByCategoryForUser(category, user.getId()).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{parentId}/children")
    public List<ProjectDTO> getChildren(
            @PathVariable String parentId,
            @RequestParam ProjectCategory category,
            @AuthenticationPrincipal UserEntity user) {
        return projectService.getChildProjectsForUser(parentId, category, user.getId()).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return projectService.getProjectByIdForUser(id, user.getId())
                .map(projectMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProjectDTO saveProject(
            @RequestBody ProjectDTO projectDTO,
            @AuthenticationPrincipal UserEntity user) {
        ProjectEntity entity = projectMapper.toEntity(projectDTO);
        ProjectEntity saved = projectService.saveProjectForUser(entity, user.getId());
        return projectMapper.toDTO(saved);
    }

    @PostMapping("/link")
    public ResponseEntity<Void> linkProjects(
            @RequestParam String parentId,
            @RequestBody List<String> childIds,
            @RequestParam ProjectCategory category) {
        boolean success = projectService.linkProjects(parentId, childIds, category);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProjectDTO> updateProject(
            @PathVariable String id,
            @RequestBody ProjectDTO projectDTO,
            @AuthenticationPrincipal UserEntity user) {
        ProjectEntity updates = projectMapper.toEntity(projectDTO);
        ProjectEntity saved = projectService.updateProjectMetadata(id, updates, user.getId());
        return ResponseEntity.ok(projectMapper.toDTO(saved));
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<ProjectDTO> toggleArchive(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        ProjectEntity saved = projectService.toggleArchive(id, user.getId());
        return ResponseEntity.ok(projectMapper.toDTO(saved));
    }

    @PatchMapping("/{id}/favorite")
    public ResponseEntity<ProjectDTO> toggleFavorite(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        ProjectEntity saved = projectService.toggleFavorite(id, user.getId());
        return ResponseEntity.ok(projectMapper.toDTO(saved));
    }

    @PostMapping("/unlink")
    public ResponseEntity<Void> unlinkProject(
            @RequestParam String parentId,
            @RequestParam String childId) {
        boolean success = projectService.unlinkProject(parentId, childId);
        return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
    }

    @PostMapping("/{id}/duplicate")
    public ResponseEntity<ProjectDTO> duplicateProject(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        ProjectEntity saved = projectService.duplicateProject(id, user.getId());
        return ResponseEntity.ok(projectMapper.toDTO(saved));
    }

    @GetMapping("/{parentId}/descendants")
    public List<ProjectDTO> getDescendants(@PathVariable String parentId) {
        return projectService.getDescendantProjects(parentId).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
