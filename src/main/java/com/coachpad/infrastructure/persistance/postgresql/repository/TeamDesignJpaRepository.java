package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.TeamDesignEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeamDesignJpaRepository extends JpaRepository<TeamDesignEntity, Long> {

    @EntityGraph(attributePaths = {"colors"})
    Optional<TeamDesignEntity> findByTeamId(Long teamId);
}
