package com.coachpad.persistence.repository;

import com.coachpad.persistence.Enum.DesignStyle;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.entity.TeamDesignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamDesignRepository extends JpaRepository<TeamDesignEntity, Long> {

    /**
     * Trouve les designs par style
     */
    List<TeamDesignEntity> findByStyle(String style);

    /**
     * Trouve les designs par type de maillot
     */
    List<TeamDesignEntity> findByJerseyDesign(String jerseyDesign);

    /**
     * Trouve un design par équipe
     */
    @Query("SELECT d FROM TeamDesignEntity d WHERE d.team.id = :teamId")
    Optional<TeamDesignEntity> findByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les designs avec logo personnalisé
     */
    @Query("SELECT d FROM TeamDesignEntity d WHERE d.logoFilePath IS NOT NULL")
    List<TeamDesignEntity> findDesignsWithCustomLogo();

    /**
     * Trouve les designs avec icône
     */
    @Query("SELECT d FROM TeamDesignEntity d WHERE d.logoIconName IS NOT NULL")
    List<TeamDesignEntity> findDesignsWithIconLogo();

    /**
     * Trouve un design avec ses couleurs (fetch eager)
     */
    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.colors WHERE d.id = :id")
    Optional<TeamDesignEntity> findByIdWithColors(@Param("id") Long id);

    /**
     * Trouve un design avec son équipe (fetch eager)
     */
    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.team WHERE d.id = :id")
    Optional<TeamDesignEntity> findByIdWithTeam(@Param("id") Long id);

    /**
     * Trouve tous les designs avec leurs couleurs
     */
    @Query("SELECT DISTINCT d FROM TeamDesignEntity d LEFT JOIN FETCH d.colors")
    List<TeamDesignEntity> findAllWithColors();

    /**
     * Compte les designs par style
     */
    @Query("SELECT COUNT(d) FROM TeamDesignEntity d WHERE d.style = :style")
    long countByStyle(@Param("style") DesignStyle style);

    /**
     * Compte les designs par type de maillot
     */
    @Query("SELECT COUNT(d) FROM TeamDesignEntity d WHERE d.jerseyDesign = :jerseyDesign")
    long countByJerseyDesign(@Param("jerseyDesign") JerseyDesign jerseyDesign);
}