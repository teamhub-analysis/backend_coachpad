package com.coachpad.mapper;

import com.coachpad.dto.TeamDTO;
import com.coachpad.persistence.entity.*;
import org.mapstruct.*;
import java.util.List;

@Mapper(
    componentModel = "spring",
    uses = {PlayerMapper.class, TeamDesignMapper.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface TeamMapper {


    @Mapping(target = "formationId", source = "formation.id")
    @Mapping(target = "formationName", source = "formation.formationFormat")
    @Mapping(target = "headCoachId", source = "headCoach.id")
    @Mapping(target = "headCoachName", expression = "java(entity.getHeadCoach() != null ? entity.getHeadCoach().getFirstName() + \" \" + entity.getHeadCoach().getLastName() : null)")
    @Mapping(target = "designId", source = "design.id")
    @Mapping(target = "design", qualifiedByName = "toDTO") // utilise TeamDesignMapper
@Mapping(
    target = "playerIds",
    expression = "java(entity.getPlayers() != null ? entity.getPlayers().stream().map(p -> p.getId()).toList() : null)"
)
    @Mapping(target = "players", source = "players")
    @Mapping(target = "playerCount", expression = "java(entity.getPlayers() != null ? entity.getPlayers().size() : 0)")
    TeamDTO toDTO(TeamEntity entity);

    List<TeamDTO> toDTOList(List<TeamEntity> entities);

    // ==========================================================
    // 🔹 DTO → Entity (sans joueurs)
    // ==========================================================

    @Mapping(target = "formation", expression = "java(dto.getFormationId() != null ? FormationEntity.builder().id(dto.getFormationId()).build() : null)")
    @Mapping(target = "headCoach", expression = "java(dto.getHeadCoachId() != null ? CoachEntity.builder().id(dto.getHeadCoachId()).build() : null)")
    @Mapping(target = "design", source = "design")
    @Mapping(target = "players", ignore = true)
    TeamEntity toEntity(TeamDTO dto);

    List<TeamEntity> toEntityList(List<TeamDTO> dtos);

    // ==========================================================
    // 🔹 Mise à jour partielle
    // ==========================================================

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "formation", ignore = true)
    @Mapping(target = "headCoach", ignore = true)
    @Mapping(target = "design", ignore = true)
    @Mapping(target = "players", ignore = true)
    void updateEntityFromDTO(TeamDTO dto, @MappingTarget TeamEntity entity);

}
