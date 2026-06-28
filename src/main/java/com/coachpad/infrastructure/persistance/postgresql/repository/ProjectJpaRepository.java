package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.domain.model.enums.ProjectCategory;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour Projects
 * Supporte le filtrage strict par catégorie pour respecter la hiérarchie.
 */
@Repository
public interface ProjectJpaRepository extends JpaRepository<ProjectEntity, String> {

    // ===== USER-SCOPED QUERIES =====
    
    // Récupérer tous les projets d'un utilisateur
    List<ProjectEntity> findByUserId(Long userId);

    // Récupérer les projets d'un utilisateur par catégorie
    List<ProjectEntity> findByUserIdAndCategory(Long userId, ProjectCategory category);

    // Récupérer les enfants d'un projet filtré par catégorie et utilisateur
    List<ProjectEntity> findByParentIdAndCategoryAndUserId(String parentId, ProjectCategory category, Long userId);

    // Récupérer tous les enfants d'un projet pour un utilisateur
    List<ProjectEntity> findByParentIdAndUserId(String parentId, Long userId);

    // ===== LEGACY (NON-SCOPED) QUERIES =====

    // Récupérer les enfants d'un projet filtré par catégorie
    List<ProjectEntity> findByParentIdAndCategory(String parentId, ProjectCategory category);

    // Récupérer tous les projets d'une catégorie
    List<ProjectEntity> findByCategory(ProjectCategory category);

    // Récupérer les templates d'une catégorie
    List<ProjectEntity> findByCategoryAndIsTemplateTrue(ProjectCategory category);

    // Récupérer les favoris d'une catégorie
    List<ProjectEntity> findByCategoryAndIsFavoriteTrue(ProjectCategory category);

    // Récupérer tous les enfants d'un projet
    List<ProjectEntity> findByParentId(String parentId);
}
