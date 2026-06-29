package com.coachpad.infrastructure.service.project;

import com.coachpad.presentation.rest.dto.ProjectContentDTO;
import com.coachpad.presentation.rest.dto.SceneDTO;
import com.coachpad.infrastructure.persistance.postgresql.mapper.SceneEntityMapper;
import com.coachpad.infrastructure.persistance.postgresql.entity.SceneEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.ProjectJpaRepository;
import com.coachpad.infrastructure.persistance.postgresql.repository.SceneJpaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion du contenu tactique complet d'un projet.
 * GÃƒÆ’Ã‚Â¨re la synchronisation entre ProjectEntity et ses SceneEntities.
 */
@Service
@RequiredArgsConstructor
public class TacticalDataService {

    private final ProjectJpaRepository projectJpaRepository;
    private final SceneJpaRepository sceneJpaRepository;
    private final SceneEntityMapper sceneMapper;

    /**
     * RÃƒÆ’Ã‚Â©cupÃƒÆ’Ã‚Â¨re le contenu complet d'un projet (mÃƒÆ’Ã‚Â©tadonnÃƒÆ’Ã‚Â©es + scÃƒÆ’Ã‚Â¨nes).
     */
    public Optional<ProjectContentDTO> getProjectContent(String projectId) {
        return projectJpaRepository.findById(projectId).map(project -> {
            List<SceneEntity> sceneEntities = sceneJpaRepository.findByProjectIdOrderByOrderIndexAsc(projectId);
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
     * Met ÃƒÆ’Ã‚Â  jour les mÃƒÆ’Ã‚Â©tadonnÃƒÆ’Ã‚Â©es globales et synchronise les scÃƒÆ’Ã‚Â¨nes.
     */
    @Transactional
    public void saveProjectContent(ProjectContentDTO content) {
        Optional<ProjectEntity> projectOpt = projectJpaRepository.findById(content.getProjectId());
        if (projectOpt.isEmpty())
            return;

        ProjectEntity project = projectOpt.get();

        // 1. Mise ÃƒÆ’Ã‚Â  jour des mÃƒÆ’Ã‚Â©tadonnÃƒÆ’Ã‚Â©es globales
        project.setFormation(content.getFormation());
        project.setHomeTeamId(content.getHomeTeamId());
        project.setAwayTeamId(content.getAwayTeamId());
        project.setHomeTeamName(content.getHomeTeamName());
        project.setAwayTeamName(content.getAwayTeamName());
        project.setExerciseIds(content.getExerciseIds());
        project.setSessionIds(content.getSessionIds());

        // Mise ÃƒÆ’Ã‚Â  jour des compteurs statistiques
        project.setSceneCount(content.getScenes().size());
        project.setModificationCount(project.getModificationCount() + 1);

        projectJpaRepository.save(project);

        // 2. Synchronisation des scÃƒÆ’Ã‚Â¨nes (Suppression puis insertion pour simplicitÃƒÆ’Ã‚Â©)
        // Note: Dans une application massive, on ferait un diff, mais ici le pack
        // complet est envoyÃƒÆ’Ã‚Â©.
        sceneJpaRepository.deleteByProjectId(content.getProjectId());

        List<SceneEntity> sceneEntities = content.getScenes().stream()
                .map(dto -> {
                    SceneEntity entity = sceneMapper.toEntity(dto);
                    entity.setProjectId(content.getProjectId());
                    return entity;
                })
                .collect(Collectors.toList());

        sceneJpaRepository.saveAll(sceneEntities);
    }
}
