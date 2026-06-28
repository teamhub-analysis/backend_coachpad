package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.domain.model.enums.JerseyDesign;
import com.coachpad.domain.model.enums.WidgetAppearance;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamDesignEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamDesignJpaRepository extends JpaRepository<TeamDesignEntity, Long> {

    // === RECHERCHES AVEC EAGER LOADING DES COULEURS ===
    
    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.colors")
    List<TeamDesignEntity> findAllWithColors();

    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.colors WHERE d.id = :id")
    Optional<TeamDesignEntity> findByIdWithColors(@Param("id") Long id);

    // === RECHERCHES PAR TEAM ===
    
    @EntityGraph(attributePaths = {"colors"})
    Optional<TeamDesignEntity> findByTeamId(Long teamId);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM TeamDesignEntity d WHERE d.team.id = :teamId")
    boolean existsByTeamId(@Param("teamId") Long teamId);

    // === RECHERCHES PAR STYLE (ENUM) ===
    
    @EntityGraph(attributePaths = {"colors"})
    List<TeamDesignEntity> findByStyle(WidgetAppearance style);

    @Query("SELECT DISTINCT d.style FROM TeamDesignEntity d WHERE d.style IS NOT NULL")
    List<WidgetAppearance> findAllDistinctStyles();

    @Query("SELECT COUNT(d) FROM TeamDesignEntity d WHERE d.style = :style")
    long countByStyle(@Param("style") WidgetAppearance style);

    // === RECHERCHES PAR JERSEY DESIGN (ENUM) ===
    
    @EntityGraph(attributePaths = {"colors"})
    List<TeamDesignEntity> findByJerseyDesign(JerseyDesign jerseyDesign);

    @Query("SELECT DISTINCT d.jerseyDesign FROM TeamDesignEntity d WHERE d.jerseyDesign IS NOT NULL")
    List<JerseyDesign> findAllDistinctJerseyDesigns();

    @Query("SELECT COUNT(d) FROM TeamDesignEntity d WHERE d.jerseyDesign = :jerseyDesign")
    long countByJerseyDesign(@Param("jerseyDesign") JerseyDesign jerseyDesign);

    // === RECHERCHES COMBINÉES ===
    
    @Query("""
        SELECT DISTINCT d FROM TeamDesignEntity d 
        LEFT JOIN FETCH d.colors 
        WHERE d.style = :style 
        AND d.jerseyDesign = :jerseyDesign
        """)
    List<TeamDesignEntity> findByStyleAndJerseyDesign(
            @Param("style") WidgetAppearance style, 
            @Param("jerseyDesign") JerseyDesign jerseyDesign
    );

    // === RECHERCHES PAR LOGO ===
    
    @Query("SELECT d FROM TeamDesignEntity d WHERE d.logoFilePath IS NOT NULL")
    List<TeamDesignEntity> findDesignsWithCustomLogo();

    @Query("SELECT d FROM TeamDesignEntity d WHERE d.logoIconName IS NOT NULL")
    List<TeamDesignEntity> findDesignsWithIconLogo();
    
    @Query("SELECT COUNT(d) FROM TeamDesignEntity d WHERE d.logoFilePath IS NOT NULL")
    long countDesignsWithCustomLogo();

    // === AVEC TEAM (EAGER LOADING) ===
    
    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.team WHERE d.id = :id")
    Optional<TeamDesignEntity> findByIdWithTeam(@Param("id") Long id);

    // === STATISTIQUES ===
    
    @Query("SELECT COUNT(d) FROM TeamDesignEntity d")
    long countAllDesigns();
}
