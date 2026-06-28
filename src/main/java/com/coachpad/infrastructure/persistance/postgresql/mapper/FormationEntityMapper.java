package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.FormationDTO;
import com.coachpad.domain.model.FormationModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.FormationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FormationEntityMapper {

    FormationModel toModel(FormationEntity entity);


    FormationEntity toEntity(FormationModel model);

    FormationEntity toEntity(FormationDTO dto);

    FormationDTO toDto(FormationEntity entity);
}
