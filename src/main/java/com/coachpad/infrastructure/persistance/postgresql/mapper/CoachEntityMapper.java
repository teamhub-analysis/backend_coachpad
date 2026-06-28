package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.CoachDTO;
import com.coachpad.domain.model.CoachModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.CoachEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CoachEntityMapper {

    CoachModel toModel(CoachEntity entity);
    CoachEntity toEntity(CoachModel model);
    CoachEntity toEntity(CoachDTO dto);
    CoachDTO toDTO(CoachModel model);
    CoachModel toModel(CoachDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(CoachDTO dto, @MappingTarget CoachEntity entity);
}
