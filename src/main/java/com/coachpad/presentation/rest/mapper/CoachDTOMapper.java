package com.coachpad.presentation.rest.mapper;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.presentation.rest.dto.CoachDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CoachDTOMapper {
    CoachDTO toDTO(CoachModel model);
    CoachModel toModel(CoachDTO dto);
}
