package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.CoachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CoachJpaRepository extends JpaRepository<CoachEntity, Long> {
    List<CoachEntity> findByTeamId(Long teamId);
}
