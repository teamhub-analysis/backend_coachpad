package com.coachpad.persistence;

import com.coachpad.model.SceneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SceneRepository extends JpaRepository<SceneEntity, String> {

    List<SceneEntity> findByProjectIdOrderByOrderIndexAsc(String projectId);

    void deleteByProjectId(String projectId);
}
