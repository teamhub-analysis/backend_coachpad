package com.coachpad.infrastructure.persistance.postgresql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.coachpad.infrastructure.persistance.postgresql.entity.SceneEntity;

import java.util.List;

public interface SceneJpaRepository extends JpaRepository<SceneEntity, String> {

    List<SceneEntity> findByProjectIdOrderByOrderIndexAsc(String projectId);

    void deleteByProjectId(String projectId);
}
