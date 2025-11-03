package com.coachpad.mapper;

import com.coachpad.dto.TeamDTO;
import com.coachpad.persistence.entity.CoachEntity;
import com.coachpad.persistence.entity.FormationEntity;
import com.coachpad.persistence.entity.PlayerEntity;
import com.coachpad.persistence.entity.TeamEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TeamMapper {

    private final TeamDesignMapper teamDesignMapper;

    /**
     * Convertit une entité Team en DTO
     */
    public TeamDTO toDTO(TeamEntity entity) {
        if (entity == null) {
            return null;
        }

        return TeamDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .formationId(entity.getFormation() != null ? entity.getFormation().getId() : null)
                .formationName(entity.getFormation() != null ? entity.getFormation().getName() : null)
                .headCoachId(entity.getHeadCoach() != null ? entity.getHeadCoach().getId() : null)
                .headCoachName(entity.getHeadCoach() != null ? 
                    entity.getHeadCoach().getFirstName() + " " + entity.getHeadCoach().getLastName() : null)
                .designId(entity.getDesign() != null ? entity.getDesign().getId() : null)
                .design(entity.getDesign() != null ? teamDesignMapper.toDTO(entity.getDesign()) : null)
                .playerIds(entity.getPlayers() != null ? 
                    entity.getPlayers().stream()
                        .map(PlayerEntity::getId)
                        .collect(Collectors.toList()) : null)
                .playerCount(entity.getPlayers() != null ? entity.getPlayers().size() : 0)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertit un DTO en entité Team
     */
    public TeamEntity toEntity(TeamDTO dto) {
        if (dto == null) {
            return null;
        }

        TeamEntity entity = TeamEntity.builder()
                .id(dto.getId())
                .name(dto.getName())
                .nickname(dto.getNickname())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        // Les relations doivent être gérées séparément dans l'adapter
        if (dto.getFormationId() != null) {
            FormationEntity formation = new FormationEntity();
            formation.setId(dto.getFormationId());
            entity.setFormation(formation);
        }

        if (dto.getHeadCoachId() != null) {
            CoachEntity coach = new CoachEntity();
            coach.setId(dto.getHeadCoachId());
            entity.setHeadCoach(coach);
        }

        if (dto.getDesign() != null) {
            entity.setDesign(teamDesignMapper.toEntity(dto.getDesign()));
        }

        return entity;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     */
    public void updateEntityFromDTO(TeamDTO dto, TeamEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setName(dto.getName());
        entity.setNickname(dto.getNickname());

        // Les relations sont gérées dans l'adapter
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     */
    public List<TeamDTO> toDTOList(List<TeamEntity> entities) {
        if (entities == null) {
            return null;
        }
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convertit une liste de DTOs en liste d'entités
     */
    public List<TeamEntity> toEntityList(List<TeamDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}