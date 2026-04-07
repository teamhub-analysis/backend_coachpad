package com.coachpad.mapper;

import com.coachpad.dto.CoachDTO;
import com.coachpad.model.CoachModel;
import com.coachpad.persistence.entity.CoachEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE) // ✅ ignore automatiquement les propriétés non mappées
public interface CoachMapper {

    CoachModel toModel(CoachEntity entity);
    CoachEntity toEntity(CoachModel model);
    CoachEntity toEntity(CoachDTO dto); // ✅ AJOUT
    CoachDTO toDTO(CoachModel model);
    CoachModel toModel(CoachDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CoachDTO dto, @MappingTarget CoachEntity entity);
}
