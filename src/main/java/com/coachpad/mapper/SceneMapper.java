package com.coachpad.mapper;

import com.coachpad.dto.SceneDTO;
import com.coachpad.model.SceneEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SceneMapper {

    @Mapping(source = "orderIndex", target = "order")
    @Mapping(source = "durationMs", target = "duration")
    SceneDTO toDTO(SceneEntity entity);

    @Mapping(source = "order", target = "orderIndex")
    @Mapping(source = "duration", target = "durationMs")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    SceneEntity toEntity(SceneDTO dto);
}
