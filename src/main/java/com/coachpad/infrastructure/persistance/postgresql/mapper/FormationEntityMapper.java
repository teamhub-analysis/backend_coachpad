package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.FormationDTO;
import com.coachpad.domain.model.FormationModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.FormationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FormationEntityMapper {

    FormationModel toModel(FormationEntity entity);

    @Mapping(target = "teams", ignore = true)
    FormationEntity toEntity(FormationModel model);

    @Mapping(target = "teams", ignore = true)
    FormationEntity toEntity(FormationDTO dto);

    FormationDTO toDto(FormationEntity entity);
}
