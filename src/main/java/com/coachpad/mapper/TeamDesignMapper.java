package com.coachpad.mapper;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.persistence.entity.TeamDesignEntity;
import com.coachpad.persistence.entity.TeamEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {TeamKitColorsMapper.class})
public interface TeamDesignMapper {

    // === Entity â†’ DTO ===
    @Named("toDTO")
    @Mapping(source = "team.id", target = "teamId")
    TeamDesignDTO toDTO(TeamDesignEntity entity);

    // === DTO â†’ Entity (crÃ©ation) ===
    @Mapping(target = "id", ignore = true)
    @Mapping(source = "teamId", target = "team.id")
    TeamDesignEntity toEntity(TeamDesignDTO dto);

    // === Mise Ã  jour partielle ===
    @Mapping(target = "team", ignore = true)
    @Mapping(target = "id", ignore = true)
    
    void updateEntityFromDTO(TeamDesignDTO dto, @MappingTarget TeamDesignEntity entity);
    
    // === MÃ©thode helper pour mapper teamId â†’ TeamEntity ===
    /**
     * Convertit un teamId en TeamEntity (juste avec l'ID, sans charger l'entitÃ© complÃ¨te)
     * Cette mÃ©thode est utilisÃ©e automatiquement par MapStruct lors du mapping
     */
    default TeamEntity map(Long teamId) {
        if (teamId == null) {
            return null;
        }
        TeamEntity team = new TeamEntity();
        team.setId(teamId);
        return team;
    }
}
