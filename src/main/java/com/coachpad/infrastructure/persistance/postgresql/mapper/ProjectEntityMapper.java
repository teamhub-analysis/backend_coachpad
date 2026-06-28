package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.ProjectDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    uses = SceneEntityMapper.class,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ProjectEntityMapper {

    ProjectDTO toDTO(ProjectEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    ProjectEntity toEntity(ProjectDTO dto);
}
