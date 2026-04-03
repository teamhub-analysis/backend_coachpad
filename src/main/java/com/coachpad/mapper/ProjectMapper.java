package com.coachpad.mapper;

import com.coachpad.dto.ProjectDTO;
import com.coachpad.persistence.entity.ProjectEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDTO toDTO(ProjectEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    ProjectEntity toEntity(ProjectDTO dto);
}
