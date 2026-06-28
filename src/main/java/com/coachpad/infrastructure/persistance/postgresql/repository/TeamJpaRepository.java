// src/main/java/com/coachpad/infrastructure/persistance/postgresql/repository/TeamJpaRepository.java
package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.TeamEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamJpaRepository extends JpaRepository<TeamEntity, Long> {

    // === RECHERCHES BASIQUES ===
    Optional<TeamEntity> findByName(String name);

    boolean existsByName(String name);

    Optional<TeamEntity> findByNickname(String nickname);

    @Query("SELECT t FROM TeamEntity t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<TeamEntity> searchByName(@Param("name") String name);

    List<TeamEntity> findByFormationId(Long formationId);

    @Query("SELECT t FROM TeamEntity t JOIN t.coaches c WHERE c.id = :coachId")
    Optional<TeamEntity> findByHeadCoachId(@Param("coachId") Long coachId);

    // === FILTRES MÉTIER ===
    List<TeamEntity> findBySource(String source);

    @Query("SELECT t FROM TeamEntity t WHERE NOT EXISTS (SELECT c FROM CoachEntity c WHERE c.team = t)")
    List<TeamEntity> findTeamsWithoutCoach();

    @Query("SELECT t FROM TeamEntity t WHERE SIZE(t.players) >= :minPlayers")
    List<TeamEntity> findTeamsWithMinimumPlayers(@Param("minPlayers") Integer minPlayers);

    @Query("SELECT t FROM TeamEntity t WHERE SIZE(t.players) <= :maxPlayers")
    List<TeamEntity> findTeamsWithMaximumPlayers(@Param("maxPlayers") Integer maxPlayers);

    // === STATISTIQUES ===
    @Query("SELECT COUNT(t) FROM TeamEntity t")
    long countAllTeams();

    // === FETCH EAGER AVEC EntityGraph (recommandé) ===
    @EntityGraph(attributePaths = { "players" })
    Optional<TeamEntity> findWithPlayersById(Long id);

    @EntityGraph(attributePaths = { "coaches" })
    Optional<TeamEntity> findWithCoachById(Long id);

    @EntityGraph(attributePaths = { "design", "design.colors" })
    Optional<TeamEntity> findWithDesignById(Long id);

    @EntityGraph(attributePaths = { "coaches", "design", "design.colors", "formation", "players", "groups", "medicalStaff" })
    Optional<TeamEntity> findWithAllRelationsById(Long id);

    @Query("SELECT t FROM TeamEntity t")
    @EntityGraph(attributePaths = { "players" })
    List<TeamEntity> findAllWithPlayers();

    @Query("SELECT t FROM TeamEntity t")
    @EntityGraph(attributePaths = { "coaches" })
    List<TeamEntity> findAllWithCoaches();

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
