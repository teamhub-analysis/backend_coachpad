package com.coachpad.presentation.rest.mapper;

import com.coachpad.presentation.rest.dto.TeamKitColorsDTO;
import com.coachpad.domain.model.TeamKitColorsModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamKitColorsDTOMapper {
    TeamKitColorsDTO toDTO(TeamKitColorsModel model);
    TeamKitColorsModel toModel(TeamKitColorsDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(TeamKitColorsDTO dto, @MappingTarget TeamKitColorsModel model);
}
