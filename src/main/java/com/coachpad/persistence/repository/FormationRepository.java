package com.coachpad.persistence.repository;

import com.coachpad.persistence.entity.FormationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationRepository extends JpaRepository<FormationEntity, Long> {

    /**
     * Trouve une formation par nom exact
     */
    Optional<FormationEntity> findByName(String name);

    /**
     * Vérifie si un nom de formation existe
     */
    boolean existsByName(String name);

    /**
     * Recherche des formations par nom (contient)
     */
    @Query("SELECT f FROM FormationEntity f WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<FormationEntity> searchByName(@Param("name") String name);

    /**
     * Trouve toutes les formations valides (11 joueurs avec 1 gardien)
     */
    @Query("SELECT f FROM FormationEntity f WHERE SIZE(f.orderedPositions) = 11")
    List<FormationEntity> findValidFormations();

    /**
     * Trouve les formations les plus utilisées
     */
    @Query("SELECT f FROM FormationEntity f ORDER BY SIZE(f.teams) DESC")
    List<FormationEntity> findMostUsedFormations();

    /**
     * Trouve les formations non utilisées
     */
    @Query("SELECT f FROM FormationEntity f WHERE SIZE(f.teams) = 0")
    List<FormationEntity> findUnusedFormations();

    /**
     * Compte le nombre d'équipes utilisant une formation
     */
    @Query("SELECT COUNT(t) FROM TeamEntity t WHERE t.formation.id = :formationId")
    long countTeamsUsingFormation(@Param("formationId") Long formationId);

    /**
     * Trouve une formation avec ses équipes (fetch eager)
     */
    @Query("SELECT DISTINCT f FROM FormationEntity f LEFT JOIN FETCH f.teams WHERE f.id = :id")
    Optional<FormationEntity> findByIdWithTeams(@Param("id") Long id);

    /**
     * Trouve toutes les formations avec leurs positions
     */
    @Query("SELECT DISTINCT f FROM FormationEntity f LEFT JOIN FETCH f.orderedPositions")
    List<FormationEntity> findAllWithPositions();
}