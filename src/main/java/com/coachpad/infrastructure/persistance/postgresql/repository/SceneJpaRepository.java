package com.coachpad.infrastructure.persistance.postgresql.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.SceneEntity;

import java.util.List;

@Repository
public interface SceneJpaRepository extends JpaRepository<SceneEntity, String> {

    List<SceneEntity> findByProjectIdOrderByOrderIndexAsc(String projectId);

    void deleteByProjectId(String projectId);
}
