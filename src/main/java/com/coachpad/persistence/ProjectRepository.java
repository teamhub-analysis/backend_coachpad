package com.coachpad.persistence;

import com.coachpad.model.ProjectEntity;
import com.coachpad.model.enums.ProjectCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour Projects
 * Supporte le filtrage strict par catégorie pour respecter la hiérarchie.
 */
@Repository
public interface ProjectRepository extends JpaRepository<ProjectEntity, String> {

    // Récupérer les enfants d'un projet filtré par catégorie
    List<ProjectEntity> findByParentIdAndCategory(String parentId, ProjectCategory category);

    // Récupérer tous les projets d'une catégorie
    List<ProjectEntity> findByCategory(ProjectCategory category);

    // Récupérer les templates d'une catégorie
    List<ProjectEntity> findByCategoryAndIsTemplateTrue(ProjectCategory category);

    // Récupérer les favoris d'une catégorie
    List<ProjectEntity> findByCategoryAndIsFavoriteTrue(ProjectCategory category);
}
