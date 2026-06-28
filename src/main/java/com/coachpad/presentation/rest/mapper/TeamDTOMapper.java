package com.coachpad.presentation.rest.mapper;

import com.coachpad.presentation.rest.dto.TeamDTO;
import com.coachpad.domain.model.TeamModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {CoachDTOMapper.class, PlayerDTOMapper.class, SquadGroupDTOMapper.class})
public interface TeamDTOMapper {
    TeamDTO toDTO(TeamModel model);
    TeamModel toModel(TeamDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(TeamDTO dto, @MappingTarget TeamModel model);
}
