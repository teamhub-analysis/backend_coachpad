package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.PlayerEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerEntityMapper {

    @Mapping(target = "teamId", source = "team.id")
    @Mapping(target = "category", ignore = true)
    PlayerDTO toDTO(PlayerEntity entity);

    @Mapping(target = "team", ignore = true)
    PlayerEntity toEntity(PlayerDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "team", ignore = true)
    void updateEntityFromDTO(PlayerDTO dto, @MappingTarget PlayerEntity entity);

    List<PlayerDTO> toDTOList(List<PlayerEntity> entities);

    List<PlayerEntity> toEntityList(List<PlayerDTO> dtos);
}
