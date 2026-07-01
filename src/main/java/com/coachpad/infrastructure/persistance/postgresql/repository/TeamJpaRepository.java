package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.TeamEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TeamJpaRepository extends JpaRepository<TeamEntity, Long> {

    boolean existsByName(String name);

    @EntityGraph(attributePaths = { "design", "design.colors", "formation" })
    Optional<TeamEntity> findWithAllRelationsById(Long id);
}
