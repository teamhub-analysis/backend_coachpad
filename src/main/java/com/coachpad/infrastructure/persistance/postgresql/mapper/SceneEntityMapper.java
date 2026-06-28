package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.SceneDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.SceneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SceneEntityMapper {

    @Mapping(source = "orderIndex", target = "order")
    @Mapping(source = "durationMs", target = "duration")
    SceneDTO toDTO(SceneEntity entity);

    @Mapping(source = "order", target = "orderIndex")
    @Mapping(source = "duration", target = "durationMs")
    @Mapping(target = "createdAt", ignore = true)
    SceneEntity toEntity(SceneDTO dto);
}
