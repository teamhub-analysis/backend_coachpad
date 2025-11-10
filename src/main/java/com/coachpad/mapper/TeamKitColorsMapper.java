// src/main/java/com/coachpad/mapper/TeamKitColorsMapper.java
package com.coachpad.mapper;

import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface TeamKitColorsMapper {

    // Entity → DTO
    @Mapping(target = "hasGoodContrast", expression = "java(entity.hasGoodContrast())")
    TeamKitColorsDTO toDto(TeamKitColorsEntity entity);

    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    TeamKitColorsEntity toEntity(TeamKitColorsDTO dto);

    // Mise à jour partielle
    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TeamKitColorsDTO dto, @MappingTarget TeamKitColorsEntity entity);
}