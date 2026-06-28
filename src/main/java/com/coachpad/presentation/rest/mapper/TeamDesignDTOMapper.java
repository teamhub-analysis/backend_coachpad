package com.coachpad.presentation.rest.mapper;

import com.coachpad.presentation.rest.dto.TeamDesignDTO;
import com.coachpad.domain.model.TeamDesignModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = TeamKitColorsDTOMapper.class)
public interface TeamDesignDTOMapper {
    TeamDesignDTO toDTO(TeamDesignModel model);
    TeamDesignModel toModel(TeamDesignDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(TeamDesignDTO dto, @MappingTarget TeamDesignModel model);
}
