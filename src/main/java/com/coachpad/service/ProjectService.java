package com.coachpad.service;

import com.coachpad.model.ProjectEntity;
import com.coachpad.model.enums.ProjectCategory;
import com.coachpad.persistence.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des projets et de leur hiérarchie.
 * Aligné sur la logique frontend.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectEntity> getAllProjects() {
        return projectRepository.findAll();
    }

    public List<ProjectEntity> getProjectsByCategory(ProjectCategory category) {
        return projectRepository.findByCategory(category);
    }

    public List<ProjectEntity> getChildProjects(String parentId, ProjectCategory category) {
        return projectRepository.findByParentIdAndCategory(parentId, category);
    }

    public Optional<ProjectEntity> getProjectById(String id) {
        return projectRepository.findById(id);
    }

    @Transactional
    public ProjectEntity saveProject(ProjectEntity project) {
        return projectRepository.save(project);
    }

    /**
     * Lie des enfants à un parent (Microcycle -> Sessions ou Session -> Exercises)
     */
    @Transactional
    public boolean linkProjects(String parentId, List<String> childIds, ProjectCategory childCategory) {
        Optional<ProjectEntity> parentOpt = projectRepository.findById(parentId);
        if (parentOpt.isEmpty()) return false;

        ProjectEntity parent = parentOpt.get();

        // On vérifie que les enfants existent et ont la bonne catégorie
        List<ProjectEntity> validChildren = projectRepository.findAllById(childIds)
                .stream()
                .filter(p -> p.getCategory() == childCategory)
                .toList();

        List<String> validIds = validChildren.stream().map(ProjectEntity::getId).toList();

        if (childCategory == ProjectCategory.EXERCISE) {
            parent.getExerciseIds().addAll(validIds);
            parent.setExerciseIds(parent.getExerciseIds().stream().distinct().collect(Collectors.toList()));
        } else if (childCategory == ProjectCategory.SESSION) {
            parent.getSessionIds().addAll(validIds);
            parent.setSessionIds(parent.getSessionIds().stream().distinct().collect(Collectors.toList()));
        }

        projectRepository.save(parent);
        return true;
    }

    @Transactional
    public void deleteProject(String id) {
        projectRepository.deleteById(id);
    }
}
