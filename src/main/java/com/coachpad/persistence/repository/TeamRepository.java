// src/main/java/com/coachpad/persistence/repository/TeamRepository.java
package com.coachpad.persistence.repository;

import com.coachpad.persistence.entity.TeamEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<TeamEntity, Long> {

    // === RECHERCHES BASIQUES ===
    Optional<TeamEntity> findByName(String name);
    boolean existsByName(String name);
    Optional<TeamEntity> findByNickname(String nickname);

    @Query("SELECT t FROM TeamEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TeamEntity> searchByName(@Param("name") String name);

    List<TeamEntity> findByFormationId(Long formationId);

    @Query("SELECT t FROM TeamEntity t WHERE t.headCoach.id = :coachId")
    Optional<TeamEntity> findByHeadCoachId(@Param("coachId") Long coachId);

    // === FILTRES MÉTIER ===
    @Query("SELECT t FROM TeamEntity t WHERE t.headCoach IS NULL")
    List<TeamEntity> findTeamsWithoutCoach();

    @Query("SELECT t FROM TeamEntity t WHERE SIZE(t.players) >= :minPlayers")
    List<TeamEntity> findTeamsWithMinimumPlayers(@Param("minPlayers") Integer minPlayers);

    @Query("SELECT t FROM TeamEntity t WHERE SIZE(t.players) <= :maxPlayers")
    List<TeamEntity> findTeamsWithMaximumPlayers(@Param("maxPlayers") Integer maxPlayers);

    // === STATISTIQUES ===
    @Query("SELECT COUNT(t) FROM TeamEntity t")
    long countAllTeams();

    // === FETCH EAGER AVEC EntityGraph (recommandé) ===
    @EntityGraph(attributePaths = {"players"})
    Optional<TeamEntity> findWithPlayersById(Long id);

    @EntityGraph(attributePaths = {"headCoach"})
    Optional<TeamEntity> findWithCoachById(Long id);

    @EntityGraph(attributePaths = {"design", "design.colors"})
    Optional<TeamEntity> findWithDesignById(Long id);

    @EntityGraph(attributePaths = {"players", "headCoach", "design", "design.colors", "formation"})
    Optional<TeamEntity> findWithAllRelationsById(Long id);

    @EntityGraph(attributePaths = {"players"})
    List<TeamEntity> findAllWithPlayers();

    // === ORDRE CHRONOLOGIQUE ===
    @Query("SELECT t FROM TeamEntity t ORDER BY t.createdAt DESC")
    List<TeamEntity> findRecentTeams();

    @Query("SELECT t FROM TeamEntity t ORDER BY t.updatedAt DESC")
    List<TeamEntity> findRecentlyUpdatedTeams();

    // === MÉTHODE CRITIQUE : Unicité du nom (hors ID) ===
    @Query("""
        SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END 
        FROM TeamEntity t 
        WHERE t.name = :name AND (:id IS NULL OR t.id <> :id)
        """)
    boolean existsByNameAndIdNot(@Param("name") String name, @Param("id") Long id);
}