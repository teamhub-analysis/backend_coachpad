package com.coachpad.presentation.rest.mapper;

import com.coachpad.presentation.rest.dto.SquadGroupDTO;
import com.coachpad.domain.model.SquadGroupModel;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SquadGroupDTOMapper {
    SquadGroupDTO toDTO(SquadGroupModel model);
    SquadGroupModel toModel(SquadGroupDTO dto);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateModelFromDTO(SquadGroupDTO dto, @MappingTarget SquadGroupModel model);
}
