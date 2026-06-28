package com.coachpad.presentation.rest.mapper;

import com.coachpad.domain.model.FormationModel;
import com.coachpad.presentation.rest.dto.FormationDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FormationDTOMapper {
    FormationDTO toDTO(FormationModel model);
    FormationModel toModel(FormationDTO dto);
}
