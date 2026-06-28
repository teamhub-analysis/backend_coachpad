package com.coachpad.infrastructure.persistance.postgresql.mapper;

import com.coachpad.presentation.rest.dto.TeamDesignDTO;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamDesignEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TeamKitColorsEntityMapper.class})
public interface TeamDesignEntityMapper {

    @Named("toDTO")
    @Mapping(source = "team.id", target = "teamId")
    TeamDesignDTO toDTO(TeamDesignEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "teamId", target = "team.id")
    TeamDesignEntity toEntity(TeamDesignDTO dto);

    @Mapping(target = "team", ignore = true)
    @Mapping(target = "id", ignore = true)
    
    void updateEntityFromDTO(TeamDesignDTO dto, @MappingTarget TeamDesignEntity entity);
    
    default TeamEntity map(Long teamId) {
        if (teamId == null) {
            return null;
        }
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        return team;
    }
}
