// src/main/java/com/coachpad/mapper/TeamDesignMapper.java
package com.coachpad.mapper;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.persistence.entity.TeamDesignEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TeamKitColorsMapper.class})
public interface TeamDesignMapper {

    // === Entity → DTO ===
    @Mapping(target = "hasCustomLogo", source = ".", qualifiedByName = "hasCustomLogo")
    @Mapping(target = "hasIconLogo", source = ".", qualifiedByName = "hasIconLogo")
    @Mapping(target = "fullLogoPath", source = ".", qualifiedByName = "fullLogoPath")
    @Mapping(target = "isValid", source = ".", qualifiedByName = "isValid")
    TeamDesignDTO toDTO(TeamDesignEntity entity);

    // === DTO → Entity (création) ===
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "id", ignore = true)
    TeamDesignEntity toEntity(TeamDesignDTO dto);

    // === Mise à jour partielle ===
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDTO(TeamDesignDTO dto, @MappingTarget TeamDesignEntity entity);

    // === Méthodes utilitaires pour champs calculés ===

    @Named("hasCustomLogo")
    default boolean hasCustomLogo(TeamDesignEntity entity) {
        return entity != null && entity.hasCustomLogo();
    }

    @Named("hasIconLogo")
    default boolean hasIconLogo(TeamDesignEntity entity) {
        return entity != null && entity.hasIconLogo();
    }

    @Named("fullLogoPath")
    default String fullLogoPath(TeamDesignEntity entity) {
        return entity != null ? entity.getFullLogoPath() : null;
    }

    @Named("isValid")
    default boolean isValid(TeamDesignEntity entity) {
        return entity != null && entity.isValid();
    }
}