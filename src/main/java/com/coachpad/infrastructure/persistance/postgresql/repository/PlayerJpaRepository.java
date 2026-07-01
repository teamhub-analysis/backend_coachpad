package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PlayerJpaRepository extends JpaRepository<PlayerEntity, Long> {

    List<PlayerEntity> findByTeamIdOrderByNumberAsc(Long teamId);
}
