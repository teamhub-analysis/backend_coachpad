package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Long> {

    List<PlayerEntity> findByTeamIdOrderByNumberAsc(Long teamId);
}
