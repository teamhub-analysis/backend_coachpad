package com.coachpad.service;

import com.coachpad.dto.ProjectContentDTO;
import com.coachpad.dto.SceneDTO;
import com.coachpad.mapper.SceneMapper;
import com.coachpad.model.SceneEntity;
import com.coachpad.persistence.entity.ProjectEntity;
import com.coachpad.persistence.repository.ProjectRepository;
import com.coachpad.persistence.repository.SceneRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion du contenu tactique complet d'un projet.
 * Gère la synchronisation entre ProjectEntity et ses SceneEntities.
 */
@Service
@RequiredArgsConstructor
public class TacticalDataService {

    private final ProjectRepository projectRepository;
    private final SceneRepository sceneRepository;
    private final SceneMapper sceneMapper;

    /**
     * Récupère le contenu complet d'un projet (métadonnées + scènes).
     */
    public Optional<ProjectContentDTO> getProjectContent(String projectId) {
        return projectRepository.findById(projectId).map(project -> {
            List<SceneEntity> sceneEntities = sceneRepository.findByProjectIdOrderByOrderIndexAsc(projectId);
            List<SceneDTO> scenes = sceneEntities.stream()
                    .map(sceneMapper::toDTO)
                    .collect(Collectors.toList());

            return ProjectContentDTO.builder()
                    .projectId(projectId)
                    .scenes(scenes)
                    .formation(project.getFormation())
                    .homeTeamId(project.getHomeTeamId())
                    .awayTeamId(project.getAwayTeamId())
                    .homeTeamName(project.getHomeTeamName())
                    .awayTeamName(project.getAwayTeamName())
                    .exerciseIds(project.getExerciseIds())
                    .sessionIds(project.getSessionIds())
                    .build();
        });
    }

    /**
     * Sauvegarde le contenu complet d'un projet.
     * Met à jour les métadonnées globales et synchronise les scènes.
     */
    @Transactional
    public void saveProjectContent(ProjectContentDTO content) {
        Optional<ProjectEntity> projectOpt = projectRepository.findById(content.getProjectId());
        if (projectOpt.isEmpty())
            return;

        ProjectEntity project = projectOpt.get();

        // 1. Mise à jour des métadonnées globales
        project.setFormation(content.getFormation());
        project.setHomeTeamId(content.getHomeTeamId());
        project.setAwayTeamId(content.getAwayTeamId());
        project.setHomeTeamName(content.getHomeTeamName());
        project.setAwayTeamName(content.getAwayTeamName());
        project.setExerciseIds(content.getExerciseIds());
        project.setSessionIds(content.getSessionIds());

        // Mise à jour des compteurs statistiques
        project.setSceneCount(content.getScenes().size());
        project.setModificationCount(project.getModificationCount() + 1);

        projectRepository.save(project);

        // 2. Synchronisation des scènes (Suppression puis insertion pour simplicité)
        // Note: Dans une application massive, on ferait un diff, mais ici le pack
        // complet est envoyé.
        sceneRepository.deleteByProjectId(content.getProjectId());

        List<SceneEntity> sceneEntities = content.getScenes().stream()
                .map(dto -> {
                    SceneEntity entity = sceneMapper.toEntity(dto);
                    entity.setProjectId(content.getProjectId());
                    return entity;
                })
                .collect(Collectors.toList());

        sceneRepository.saveAll(sceneEntities);
    }
}
