package com.coachpad.infrastructure.persistance.postgresql.repository;

import com.coachpad.infrastructure.persistance.postgresql.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByEmail(String email);
}
