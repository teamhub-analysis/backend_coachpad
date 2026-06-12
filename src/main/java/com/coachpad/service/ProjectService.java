package com.coachpad.service;

import com.coachpad.model.enums.ProjectCategory;
import com.coachpad.persistence.entity.ProjectEntity;
import com.coachpad.persistence.repository.ProjectRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service pour la gestion des projets et de leur hiérarchie.
 * Toutes les opérations sont filtrées par userId pour l'isolation des données.
 */
@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    // ===== USER-SCOPED OPERATIONS =====

    public List<ProjectEntity> getAllProjectsForUser(Long userId) {
        return projectRepository.findByUserId(userId);
    }

    public List<ProjectEntity> getProjectsByCategoryForUser(ProjectCategory category, Long userId) {
        return projectRepository.findByUserIdAndCategory(userId, category);
    }

    public List<ProjectEntity> getChildProjectsForUser(String parentId, ProjectCategory category, Long userId) {
        return projectRepository.findByParentIdAndCategoryAndUserId(parentId, category, userId);
    }

    public Optional<ProjectEntity> getProjectByIdForUser(String id, Long userId) {
        return projectRepository.findById(id)
                .filter(p -> userId.equals(p.getUserId()));
    }

    @Transactional
    public ProjectEntity saveProjectForUser(ProjectEntity incoming, Long userId) {
        incoming.setUserId(userId);

        // UPSERT : si le projet existe déjà, on remplace les scènes
        Optional<ProjectEntity> existing = projectRepository.findById(incoming.getId());
        if (existing.isPresent()) {
            ProjectEntity current = existing.get();
            incoming.setCreatedAt(current.getCreatedAt());

            // Remplacer les scènes (orphanRemoval = true nettoie les anciennes)
            current.getScenes().clear();
            current.getScenes().addAll(incoming.getScenes());

            // Copier tous les champs metadata du incoming vers le current
            current.setUserId(userId);
            current.setName(incoming.getName());
            current.setDescription(incoming.getDescription());
            current.setCategory(incoming.getCategory());
            current.setParentId(incoming.getParentId());
            current.setStartDate(incoming.getStartDate());
            current.setEndDate(incoming.getEndDate());
            current.setMatchDate(incoming.getMatchDate());
            current.setWeekType(incoming.getWeekType());
            current.setTags(incoming.getTags());
            current.setIntensity(incoming.getIntensity());
            current.setTimeSlot(incoming.getTimeSlot());
            current.setMicrocycleNumber(incoming.getMicrocycleNumber());
            current.setSessionNumber(incoming.getSessionNumber());
            current.setOpponentName(incoming.getOpponentName());
            current.setThumbnailBase64(incoming.getThumbnailBase64());
            current.setObjectif(incoming.getObjectif());
            current.setOrganisation(incoming.getOrganisation());
            current.setConsignes(incoming.getConsignes());
            current.setVariantes(incoming.getVariantes());
            current.setSceneCount(incoming.getSceneCount());
            current.setPlayerCount(incoming.getPlayerCount());
            current.setHomePlayerCount(incoming.getHomePlayerCount());
            current.setAwayPlayerCount(incoming.getAwayPlayerCount());
            current.setObjectCount(incoming.getObjectCount());
            current.setDrawingCount(incoming.getDrawingCount());
            current.setTotalDurationSeconds(incoming.getTotalDurationSeconds());
            current.setHasAnimations(incoming.isHasAnimations());
            current.setFormation(incoming.getFormation());
            current.setHomeTeamId(incoming.getHomeTeamId());
            current.setAwayTeamId(incoming.getAwayTeamId());
            current.setHomeTeamName(incoming.getHomeTeamName());
            current.setAwayTeamName(incoming.getAwayTeamName());
            current.setFavorite(incoming.isFavorite());
            current.setArchived(incoming.isArchived());
            current.setTemplate(incoming.isTemplate());
            current.setViewCount(incoming.getViewCount());
            current.setModificationCount(incoming.getModificationCount());
            current.setCommentsCount(incoming.getCommentsCount());
            current.setLastViewed(incoming.getLastViewed());
            current.setLastExport(incoming.getLastExport());
            current.setExerciseIds(incoming.getExerciseIds());
            current.setSessionIds(incoming.getSessionIds());
            current.setLastModified(incoming.getLastModified());

            return projectRepository.save(current);
        }

        // Nouveau projet
        incoming.setCreatedAt(incoming.getCreatedAt() != null ? incoming.getCreatedAt() : LocalDateTime.now());
        incoming.setLastModified(incoming.getLastModified() != null ? incoming.getLastModified() : LocalDateTime.now());
        return projectRepository.save(incoming);
    }

    /**
     * Lie des enfants à un parent (Microcycle -> Sessions ou Session -> Exercises)
     */
    @Transactional
    public boolean linkProjects(String parentId, List<String> childIds, ProjectCategory childCategory) {
        Optional<ProjectEntity> parentOpt = projectRepository.findById(parentId);
        if (parentOpt.isEmpty())
            return false;

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
    public ProjectEntity updateProject(String id, Long userId, java.util.function.Consumer<ProjectEntity> updater) {
        return getProjectByIdForUser(id, userId)
                .map(project -> {
                    updater.accept(project);
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }

    @Transactional
    public ProjectEntity updateProjectMetadata(String id, ProjectEntity updates, Long userId) {
        return getProjectByIdForUser(id, userId)
                .map(project -> {
                    if (updates.getName() != null) project.setName(updates.getName());
                    if (updates.getDescription() != null) project.setDescription(updates.getDescription());
                    if (updates.getCategory() != null) project.setCategory(updates.getCategory());
                    if (updates.getParentId() != null) project.setParentId(updates.getParentId());
                    if (updates.getStartDate() != null) project.setStartDate(updates.getStartDate());
                    if (updates.getEndDate() != null) project.setEndDate(updates.getEndDate());
                    if (updates.getMatchDate() != null) project.setMatchDate(updates.getMatchDate());
                    if (updates.getWeekType() != null) project.setWeekType(updates.getWeekType());
                    if (updates.getTags() != null) project.setTags(updates.getTags());
                    if (updates.getThumbnailBase64() != null) project.setThumbnailBase64(updates.getThumbnailBase64());
                    if (updates.getObjectif() != null) project.setObjectif(updates.getObjectif());
                    if (updates.getOrganisation() != null) project.setOrganisation(updates.getOrganisation());
                    if (updates.getConsignes() != null) project.setConsignes(updates.getConsignes());
                    if (updates.getVariantes() != null) project.setVariantes(updates.getVariantes());
                    if (updates.getFormation() != null) project.setFormation(updates.getFormation());
                    if (updates.getHomeTeamId() != null) project.setHomeTeamId(updates.getHomeTeamId());
                    if (updates.getAwayTeamId() != null) project.setAwayTeamId(updates.getAwayTeamId());
                    if (updates.getHomeTeamName() != null) project.setHomeTeamName(updates.getHomeTeamName());
                    if (updates.getAwayTeamName() != null) project.setAwayTeamName(updates.getAwayTeamName());
                    if (updates.getOpponentName() != null) project.setOpponentName(updates.getOpponentName());
                    if (updates.getIntensity() > 0) project.setIntensity(updates.getIntensity());
                    if (updates.getTimeSlot() != null) project.setTimeSlot(updates.getTimeSlot());
                    if (updates.getMicrocycleNumber() != null) project.setMicrocycleNumber(updates.getMicrocycleNumber());
                    if (updates.getSessionNumber() != null) project.setSessionNumber(updates.getSessionNumber());
                    project.setFavorite(updates.isFavorite());
                    project.setArchived(updates.isArchived());
                    project.setTemplate(updates.isTemplate());
                    if (updates.getLastModified() != null) project.setLastModified(updates.getLastModified());
                    return projectRepository.save(project);
                })
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }

    @Transactional
    public ProjectEntity toggleArchive(String id, Long userId) {
        return updateProject(id, userId, p -> p.setArchived(!p.isArchived()));
    }

    @Transactional
    public ProjectEntity toggleFavorite(String id, Long userId) {
        return updateProject(id, userId, p -> p.setFavorite(!p.isFavorite()));
    }

    @Transactional
    public boolean unlinkProject(String parentId, String childId) {
        Optional<ProjectEntity> parentOpt = projectRepository.findById(parentId);
        if (parentOpt.isEmpty()) return false;

        ProjectEntity parent = parentOpt.get();
        boolean removed = parent.getSessionIds().remove(childId);
        removed |= parent.getExerciseIds().remove(childId);

        if (removed) {
            projectRepository.save(parent);
        }
        return removed;
    }

    @Transactional
    public ProjectEntity duplicateProject(String id, Long userId) {
        return getProjectByIdForUser(id, userId)
                .map(original -> {
                    ProjectEntity copy = new ProjectEntity();
                    copy.setId(java.util.UUID.randomUUID().toString());
                    copy.setUserId(userId);
                    copy.setName(original.getName() + " (copie)");
                    copy.setDescription(original.getDescription());
                    copy.setCategory(original.getCategory());
                    copy.setParentId(original.getParentId());
                    copy.setStartDate(original.getStartDate());
                    copy.setEndDate(original.getEndDate());
                    copy.setMatchDate(original.getMatchDate());
                    copy.setWeekType(original.getWeekType());
                    copy.setTags(original.getTags() != null ? new java.util.ArrayList<>(original.getTags()) : new java.util.ArrayList<>());
                    copy.setIntensity(original.getIntensity());
                    copy.setTimeSlot(original.getTimeSlot());
                    copy.setMicrocycleNumber(original.getMicrocycleNumber());
                    copy.setSessionNumber(original.getSessionNumber());
                    copy.setOpponentName(original.getOpponentName());
                    copy.setThumbnailBase64(original.getThumbnailBase64());
                    copy.setObjectif(original.getObjectif());
                    copy.setOrganisation(original.getOrganisation());
                    copy.setConsignes(original.getConsignes());
                    copy.setVariantes(original.getVariantes());
                    copy.setFormation(original.getFormation());
                    copy.setHomeTeamId(original.getHomeTeamId());
                    copy.setAwayTeamId(original.getAwayTeamId());
                    copy.setHomeTeamName(original.getHomeTeamName());
                    copy.setAwayTeamName(original.getAwayTeamName());
                    copy.setFavorite(false);
                    copy.setArchived(false);
                    copy.setTemplate(original.isTemplate());
                    copy.setExerciseIds(original.getExerciseIds() != null ? new java.util.ArrayList<>(original.getExerciseIds()) : new java.util.ArrayList<>());
                    copy.setSessionIds(original.getSessionIds() != null ? new java.util.ArrayList<>(original.getSessionIds()) : new java.util.ArrayList<>());
                    return projectRepository.save(copy);
                })
                .orElseThrow(() -> new RuntimeException("Project not found: " + id));
    }

    public List<ProjectEntity> getDescendantProjects(String parentId) {
        List<ProjectEntity> allDescendants = new java.util.ArrayList<>();
        collectDescendants(parentId, allDescendants, new java.util.HashSet<>());
        return allDescendants;
    }

    private void collectDescendants(String parentId, List<ProjectEntity> result, java.util.Set<String> visited) {
        if (parentId == null || visited.contains(parentId)) return;
        visited.add(parentId);

        List<ProjectEntity> children = projectRepository.findByParentId(parentId);
        for (ProjectEntity child : children) {
            result.add(child);
            collectDescendants(child.getId(), result, visited);
        }
    }

    @Transactional
    public void deleteProject(String id) {
        Optional<ProjectEntity> projectOpt = projectRepository.findById(id);
        if (projectOpt.isEmpty()) return;

        ProjectEntity project = projectOpt.get();

        // 1. Supprimer les enfants liés par parentId (Macro -> Meso -> Micro)
        List<ProjectEntity> children = projectRepository.findByParentId(id);
        for (ProjectEntity child : children) {
            deleteProject(child.getId());
        }

        // 2. Supprimer les enfants liés par listes d'IDs (Micro -> Session, Session -> Exercise)
        if (project.getCategory() == ProjectCategory.MICROCYCLE && project.getSessionIds() != null) {
            for (String sessionId : project.getSessionIds()) {
                deleteProject(sessionId);
            }
        } else if (project.getCategory() == ProjectCategory.SESSION && project.getExerciseIds() != null) {
            for (String exerciseId : project.getExerciseIds()) {
                deleteProject(exerciseId);
            }
        }

        // 3. Enfin supprimer le projet lui-même
        projectRepository.deleteById(id);
    }

    // ===== LEGACY (kept for compatibility) =====

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
}
