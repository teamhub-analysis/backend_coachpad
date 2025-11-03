// src/main/java/com/coachpad/mapper/TeamKitColorsMapper.java
package com.coachpad.mapper;

import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamKitColorsMapper {

    TeamKitColorsMapper INSTANCE = Mappers.getMapper(TeamKitColorsMapper.class);

    @Mapping(target = "hasGoodContrast", source = ".", qualifiedByName = "calculateContrast")
    TeamKitColorsDTO toDto(TeamKitColorsEntity entity);

    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    TeamKitColorsEntity toEntity(TeamKitColorsDTO dto);

    // Pour les updates
    @Mapping(target = "design", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TeamKitColorsDTO dto, @MappingTarget TeamKitColorsEntity entity);

    @Named("calculateContrast")
    default boolean calculateContrast(TeamKitColorsEntity entity) {
        return entity.hasGoodContrast();
    }
}