package com.coachpad.mapper;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.persistence.entity.PlayerEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface PlayerMapper {

    PlayerDTO toDTO(PlayerEntity entity);
    @Mapping(target = "team", ignore = true) 
    PlayerEntity toEntity(PlayerDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "team", ignore = true)
    void updateEntityFromDTO(PlayerDTO dto, @MappingTarget PlayerEntity entity);

    List<PlayerDTO> toDTOList(List<PlayerEntity> entities);

    List<PlayerEntity> toEntityList(List<PlayerDTO> dtos);
}
