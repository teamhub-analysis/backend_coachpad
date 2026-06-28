package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.TeamDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = { PlayerEntityMapper.class,
        TeamDesignEntityMapper.class, CoachEntityMapper.class, SquadGroupEntityMapper.class }, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamEntityMapper {

    @Mapping(target = "formationId", source = "formation.id")
    @Mapping(target = "formationName", source = "formation.formationFormat")
    @Mapping(target = "headCoachId", expression = "java(mapHeadCoachId(entity))")
    @Mapping(target = "headCoachName", expression = "java(mapHeadCoachName(entity))")
    @Mapping(target = "designId", source = "design.id")
    @Mapping(target = "design", qualifiedByName = "toDTO")
    @Mapping(target = "playerIds", expression = "java(entity.getPlayers() != null ? entity.getPlayers().stream().map(p -> p.getId()).toList() : null)")
    @Mapping(target = "players", source = "players")
    @Mapping(target = "coaches", source = "coaches")
    @Mapping(target = "playerCount", expression = "java(entity.getPlayers() != null ? entity.getPlayers().size() : 0)")
    @Mapping(target = "groups", source = "groups")
    TeamDTO toDTO(TeamEntity entity);

    default Long mapHeadCoachId(TeamEntity entity) {
        if (entity.getCoaches() == null || entity.getCoaches().isEmpty()) {
            return null;
        }
        return entity.getCoaches().stream()
                .filter(c -> c.getRole() == com.coachpad.domain.model.enums.CoachRole.HEAD_COACH)
                .findFirst()
                .map(com.coachpad.infrastructure.persistance.postgresql.entity.CoachEntity::getId)
                .orElse(entity.getCoaches().get(0).getId());
    }

    default String mapHeadCoachName(TeamEntity entity) {
        if (entity.getCoaches() == null || entity.getCoaches().isEmpty()) {
            return null;
        }
        return entity.getCoaches().stream()
                .filter(c -> c.getRole() == com.coachpad.domain.model.enums.CoachRole.HEAD_COACH)
                .findFirst()
                .map(c -> c.getFirstName() + " " + c.getLastName())
                .orElse(entity.getCoaches().get(0).getFirstName() + " " + entity.getCoaches().get(0).getLastName());
    }

    List<TeamDTO> toDTOList(List<TeamEntity> entities);

    @Mapping(target = "formation", expression = "java(dto.getFormationId() != null ? FormationEntity.builder().id(dto.getFormationId()).build() : null)")
    @Mapping(target = "coaches", ignore = true)
    @Mapping(target = "design", source = "design")
    @Mapping(target = "players", source = "players")
    @Mapping(target = "medicalStaff", source = "medicalStaff")
    @Mapping(target = "groups", source = "groups")
    TeamEntity toEntity(TeamDTO dto);

    List<TeamEntity> toEntityList(List<TeamDTO> dtos);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_NULL)
    @Mapping(target = "formation", ignore = true)
    @Mapping(target = "coaches", ignore = true)
    @Mapping(target = "design", ignore = true)
    @Mapping(target = "players", source = "players")
    @Mapping(target = "medicalStaff", source = "medicalStaff")
    @Mapping(target = "groups", source = "groups")
    void updateEntityFromDTO(TeamDTO dto, @MappingTarget TeamEntity entity);

}
