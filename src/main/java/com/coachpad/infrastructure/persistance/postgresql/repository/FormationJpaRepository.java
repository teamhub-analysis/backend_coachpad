package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.FormationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FormationJpaRepository extends JpaRepository<FormationEntity, Long> {

    @NonNull
    List<FormationEntity> findAll();

    @Query("SELECT COUNT(t) FROM TeamEntity t WHERE t.formation.id = :formationId")
    long countTeamsUsingFormation(@Param("formationId") Long formationId);
}
