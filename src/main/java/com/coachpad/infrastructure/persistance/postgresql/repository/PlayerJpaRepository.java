package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Long> {

    /**
     * Trouve un joueur par email
     */
    Optional<PlayerEntity> findByEmail(String email);

    /**
     * Vérifie si un email existe
     */
    boolean existsByEmail(String email);

    /**
     * Trouve tous les joueurs d'une équipe
     */
    List<PlayerEntity> findByTeamId(Long teamId);

    /**
     * Trouve tous les joueurs d'une équipe ordonnés par numéro
     */
    List<PlayerEntity> findByTeamIdOrderByNumberAsc(Long teamId);

    /**
     * Trouve un joueur par numéro dans une équipe spécifique
     */
    Optional<PlayerEntity> findByNumberAndTeamId(Integer number, Long teamId);

    /**
     * Vérifie si un numéro existe déjà dans une équipe
     */
    boolean existsByNumberAndTeamId(Integer number, Long teamId);

    /**
     * Trouve tous les joueurs par position principale
     */
    List<PlayerEntity> findByMainPosition(String position);

    /**
     * Trouve tous les joueurs d'une équipe par position
     */
    List<PlayerEntity> findByTeamIdAndMainPosition(Long teamId, String position);

    /**
     * Trouve tous les joueurs par statut
     */
    List<PlayerEntity> findByStatus(String status);

    /**
     * Trouve tous les joueurs actifs d'une équipe
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId AND p.status = 'ACTIVE'")
    List<PlayerEntity> findActivePlayersByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs par nationalité
     */
    List<PlayerEntity> findByNationality(String nationality);

    /**
     * Trouve les joueurs par pied préféré
     */
    List<PlayerEntity> findByPreferredFoot(String preferredFoot);

    /**
     * Recherche des joueurs par nom (prénom ou nom)
     */
    @Query("SELECT p FROM PlayerEntity p WHERE " +
           "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(p.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<PlayerEntity> searchByName(@Param("name") String name);

    /**
     * Trouve les meilleurs buteurs d'une équipe (top N)
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId ORDER BY p.totalGoals DESC")
    List<PlayerEntity> findTopScorersByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs avec le plus de passes décisives
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId ORDER BY p.totalAssists DESC")
    List<PlayerEntity> findTopAssistersByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs avec une note minimale
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.currentRating >= :minRating ORDER BY p.currentRating DESC")
    List<PlayerEntity> findByMinimumRating(@Param("minRating") Double minRating);

    /**
     * Compte le nombre de joueurs dans une équipe
     */
    long countByTeamId(Long teamId);

    /**
     * Compte le nombre de joueurs par position dans une équipe
     */
    @Query("SELECT COUNT(p) FROM PlayerEntity p WHERE p.team.id = :teamId AND p.mainPosition = :position")
    long countByTeamIdAndPosition(@Param("teamId") Long teamId, @Param("position") String position);

    /**
     * Trouve les joueurs blessés d'une équipe
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId AND p.status = 'INJURED'")
    List<PlayerEntity> findInjuredPlayersByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs suspendus d'une équipe
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId AND p.status = 'SUSPENDED'")
    List<PlayerEntity> findSuspendedPlayersByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs disponibles (actifs) d'une équipe
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId AND p.status IN ('ACTIVE', 'AVAILABLE')")
    List<PlayerEntity> findAvailablePlayersByTeamId(@Param("teamId") Long teamId);

    /**
     * Calcule la moyenne des notes d'une équipe
     */
    @Query("SELECT AVG(p.currentRating) FROM PlayerEntity p WHERE p.team.id = :teamId AND p.currentRating IS NOT NULL")
    Double calculateAverageRatingByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les joueurs avec des statistiques minimales
     */
    @Query("SELECT p FROM PlayerEntity p WHERE p.team.id = :teamId " +
           "AND p.matchesPlayed >= :minMatches " +
           "ORDER BY p.totalGoals DESC, p.totalAssists DESC")
    List<PlayerEntity> findPlayersByMinimumMatches(@Param("teamId") Long teamId, 
                                                       @Param("minMatches") Integer minMatches);

    /**
     * Supprime tous les joueurs d'une équipe
     */
    @Modifying
    @Transactional
    void deleteByTeamId(Long teamId);
}
