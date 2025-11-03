package com.coachpad.persistence.repository;

import com.coachpad.persistence.Enum.CoachingPhilosophy;
import com.coachpad.persistence.Enum.LicenseLevel;
import com.coachpad.persistence.entity.CoachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CoachRepository extends JpaRepository<CoachEntity, Long> {

    /**
     * Trouve un coach par nom complet
     */
    Optional<CoachEntity> findByFullName(String fullName);

    /**
     * Recherche des coachs par nom (prénom ou nom)
     */
    @Query("SELECT c FROM CoachEntity c WHERE " +
           "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :name, '%')) OR " +
           "LOWER(c.fullName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<CoachEntity> searchByName(@Param("name") String name);

    /**
     * Trouve les coachs par niveau de licence
     */
    List<CoachEntity> findByLicenseLevel(LicenseLevel licenseLevel);

    /**
     * Trouve les coachs avec une licence minimale
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.licenseLevel >= :minLevel ORDER BY c.licenseLevel DESC")
    List<CoachEntity> findByMinimumLicenseLevel(@Param("minLevel") LicenseLevel minLevel);

    /**
     * Trouve les coachs par philosophie
     */
    List<CoachEntity> findByCoachingPhilosophy(CoachingPhilosophy philosophy);

    /**
     * Trouve les coachs par nationalité
     */
    List<CoachEntity> findByNationality(String nationality);

    /**
     * Trouve les coachs sans équipe (disponibles)
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.team IS NULL")
    List<CoachEntity> findAvailableCoaches();

    /**
     * Trouve les coachs avec une équipe
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.team IS NOT NULL")
    List<CoachEntity> findCoachesWithTeam();

    /**
     * Trouve un coach par son équipe
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.team.id = :teamId")
    Optional<CoachEntity> findByTeamId(@Param("teamId") Long teamId);

    /**
     * Trouve les coachs dont le contrat expire bientôt (dans X jours)
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.contractEndDate BETWEEN :today AND :deadline")
    List<CoachEntity> findCoachesWithExpiringContract(@Param("today") LocalDate today, 
                                                        @Param("deadline") LocalDate deadline);

    /**
     * Trouve les coachs dont le contrat est expiré
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.contractEndDate < :today")
    List<CoachEntity> findCoachesWithExpiredContract(@Param("today") LocalDate today);

    /**
     * Trouve les coachs avec contrat valide
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.contractEndDate IS NULL OR c.contractEndDate >= :today")
    List<CoachEntity> findCoachesWithValidContract(@Param("today") LocalDate today);

    /**
     * Compte le nombre de coachs disponibles
     */
    @Query("SELECT COUNT(c) FROM CoachEntity c WHERE c.team IS NULL")
    long countAvailableCoaches();

    /**
     * Trouve un coach avec son équipe (fetch eager)
     */
    @Query("SELECT DISTINCT c FROM CoachEntity c LEFT JOIN FETCH c.team WHERE c.id = :id")
    Optional<CoachEntity> findByIdWithTeam(@Param("id") Long id);

    /**
     * Trouve tous les coachs avec leurs équipes
     */
    @Query("SELECT DISTINCT c FROM CoachEntity c LEFT JOIN FETCH c.team")
    List<CoachEntity> findAllWithTeams();

    /**
     * Trouve les coachs avec une licence professionnelle
     */
    @Query("SELECT c FROM CoachEntity c WHERE c.licenseLevel IN ('UEFA_PRO', 'UEFA_A')")
    List<CoachEntity> findProfessionalCoaches();

    /**
     * Vérifie si un coach est déjà assigné à une équipe
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM CoachEntity c WHERE c.id = :coachId AND c.team IS NOT NULL")
    boolean isCoachAssigned(@Param("coachId") Long coachId);
}