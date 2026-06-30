package com.coachpad.presentation.rest.mapper;

import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.domain.model.PlayerModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PlayerDTOMapper {
    @Mapping(target = "teamId", source = "team.id")
    PlayerDTO toDTO(PlayerModel model);
    PlayerModel toModel(PlayerDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(PlayerDTO dto, @MappingTarget PlayerModel model);
}
