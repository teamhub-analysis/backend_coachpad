package com.coachpad.mapper;

import com.coachpad.dto.CoachDTO;
import com.coachpad.model.CoachModel;
import com.coachpad.persistence.entity.CoachEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CoachMapper {

    // --- Entity ↔ Model ---
    CoachModel toModel(CoachEntity entity);
    CoachEntity toEntity(CoachModel model);

    // --- Model ↔ DTO ---
    CoachModel toDTO(CoachModel model);
    CoachModel toModel(CoachDTO dto);
}
