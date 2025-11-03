package com.coachpad.persistence.repository;

import com.coachpad.persistence.entity.TeamKitColorsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamKitColorsRepository extends JpaRepository<TeamKitColorsEntity, Long> {

    /**
     * Récupère les couleurs d'un design par l'ID du design
     */
    @Query("SELECT c FROM TeamKitColorsEntity c WHERE c.design.id = :designId")
    Optional<TeamKitColorsEntity> findByDesignId(Long designId);

    /**
     * Vérifie si un design a déjà des couleurs associées
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM TeamKitColorsEntity c WHERE c.design.id = :designId")
    boolean existsByDesignId(Long designId);
}
