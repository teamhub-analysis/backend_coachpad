package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.TeamKitColorsDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamKitColorsEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeamKitColorsEntityMapper {

    @Mapping(target = "hasGoodContrast", expression = "java(entity.hasGoodContrast())")
    TeamKitColorsDTO toDto(TeamKitColorsEntity entity);

    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    TeamKitColorsEntity toEntity(TeamKitColorsDTO dto);

    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TeamKitColorsDTO dto, @MappingTarget TeamKitColorsEntity entity);
}
