package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.FormationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormationJpaRepository extends JpaRepository<FormationEntity, Long> {

    /**
     * Trouve toutes les formations
     */
    List<FormationEntity> findAll();

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
     * Trouve les formations non utilisées
     */
    @Query("SELECT f FROM FormationEntity f WHERE SIZE(f.teams) = 0")
    List<FormationEntity> findUnusedFormations();
}
