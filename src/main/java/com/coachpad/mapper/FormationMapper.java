package com.coachpad.mapper;

import com.coachpad.dto.FormationDTO;
import com.coachpad.model.FormationModel;
import com.coachpad.persistence.entity.FormationEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface FormationMapper {

    FormationModel toModel(FormationEntity entity);


    FormationEntity toEntity(FormationModel model);

    FormationEntity toEntity(FormationDTO dto);

    FormationDTO toDto(FormationEntity entity);
}
