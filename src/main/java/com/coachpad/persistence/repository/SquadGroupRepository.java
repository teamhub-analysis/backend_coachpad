package com.coachpad.persistence.repository;

import com.coachpad.persistence.entity.SquadGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SquadGroupRepository extends JpaRepository<SquadGroupEntity, Long> {
    List<SquadGroupEntity> findByTeamId(Long teamId);
}
