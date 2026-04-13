package com.coachpad.controller;

import com.coachpad.dto.ProjectContentDTO;
import com.coachpad.dto.ProjectDTO;
import com.coachpad.mapper.ProjectMapper;
import com.coachpad.model.enums.ProjectCategory;
import com.coachpad.persistence.entity.ProjectEntity;
import com.coachpad.service.ProjectService;
import com.coachpad.service.TacticalDataService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectMapper projectMapper;
    private final TacticalDataService tacticalDataService;

    @GetMapping
    public List<ProjectDTO> getAllProjects() {
        return projectService.getAllProjects().stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ✅ NOUVEAU : Récupérer le CONTENU complet (Scènes, Équipes)
    @GetMapping("/{id}/content")
    public ResponseEntity<ProjectContentDTO> getProjectContent(@PathVariable String id) {
        return tacticalDataService.getProjectContent(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ NOUVEAU : Sauvegarder le CONTENU complet (Scènes, Équipes)
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
    public List<ProjectDTO> getByCategory(@PathVariable ProjectCategory category) {
        return projectService.getProjectsByCategory(category).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{parentId}/children")
    public List<ProjectDTO> getChildren(
            @PathVariable String parentId,
            @RequestParam ProjectCategory category) {
        return projectService.getChildProjects(parentId, category).stream()
                .map(projectMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectDTO> getProject(@PathVariable String id) {
        return projectService.getProjectById(id)
                .map(projectMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ProjectDTO saveProject(@RequestBody ProjectDTO projectDTO) {
        ProjectEntity entity = projectMapper.toEntity(projectDTO);
        ProjectEntity saved = projectService.saveProject(entity);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable String id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }
}
