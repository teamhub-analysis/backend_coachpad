package com.coachpad.mapper;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.persistence.entity.PlayerEntity;
import com.coachpad.persistence.entity.TeamEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PlayerMapper {

    /**
     * Convertit une entité Player en DTO
     */
    public PlayerDTO toDTO(PlayerEntity entity) {
        if (entity == null) {
            return null;
        }

        return PlayerDTO.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .number(entity.getNumber())
                .dateOfBirth(entity.getDateOfBirth())
                .age(entity.getAge())
                .nationality(entity.getNationality())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .photoUrl(entity.getPhotoUrl())
                .heightCm(entity.getHeightCm())
                .weightKg(entity.getWeightKg())
                .preferredFoot(entity.getPreferredFoot())
                .mainPosition(entity.getMainPosition())
                .secondaryPositions(entity.getSecondaryPositions())
                .status(entity.getStatus())
                .matchesPlayed(entity.getMatchesPlayed())
                .totalGoals(entity.getTotalGoals())
                .totalAssists(entity.getTotalAssists())
                .currentRating(entity.getCurrentRating())
                .speedRating(entity.getSpeedRating())
                .staminaRating(entity.getStaminaRating())
                .shootingRating(entity.getShootingRating())
                .passingRating(entity.getPassingRating())
                .teamId(entity.getTeam() != null ? entity.getTeam().getId() : null)
                .teamName(entity.getTeam() != null ? entity.getTeam().getName() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /**
     * Convertit un DTO en entité Player
     */
    public PlayerEntity toEntity(PlayerDTO dto) {
        if (dto == null) {
            return null;
        }

        PlayerEntity entity = PlayerEntity.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .fullName(dto.getFullName())
                .number(dto.getNumber())
                .dateOfBirth(dto.getDateOfBirth())
                .nationality(dto.getNationality())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .photoUrl(dto.getPhotoUrl())
                .heightCm(dto.getHeightCm())
                .weightKg(dto.getWeightKg())
                .preferredFoot(dto.getPreferredFoot())
                .mainPosition(dto.getMainPosition())
                .secondaryPositions(dto.getSecondaryPositions())
                .status(dto.getStatus())
                .matchesPlayed(dto.getMatchesPlayed())
                .totalGoals(dto.getTotalGoals())
                .totalAssists(dto.getTotalAssists())
                .currentRating(dto.getCurrentRating())
                .speedRating(dto.getSpeedRating())
                .staminaRating(dto.getStaminaRating())
                .shootingRating(dto.getShootingRating())
                .passingRating(dto.getPassingRating())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();

        // La relation avec l'équipe doit être gérée séparément
        if (dto.getTeamId() != null) {
            TeamEntity team = new TeamEntity();
            team.setId(dto.getTeamId());
            entity.setTeam(team);
        }

        return entity;
    }

    /**
     * Met à jour une entité existante avec les données du DTO
     */
    public void updateEntityFromDTO(PlayerDTO dto, PlayerEntity entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setFirstName(dto.getFirstName());
        entity.setLastName(dto.getLastName());
        entity.setFullName(dto.getFullName());
        entity.setNumber(dto.getNumber());
        entity.setDateOfBirth(dto.getDateOfBirth());
        entity.setNationality(dto.getNationality());
        entity.setEmail(dto.getEmail());
        entity.setPhoneNumber(dto.getPhoneNumber());
        entity.setPhotoUrl(dto.getPhotoUrl());
        entity.setHeightCm(dto.getHeightCm());
        entity.setWeightKg(dto.getWeightKg());
        entity.setPreferredFoot(dto.getPreferredFoot());
        entity.setMainPosition(dto.getMainPosition());
        entity.setSecondaryPositions(dto.getSecondaryPositions());
        entity.setStatus(dto.getStatus());
        entity.setMatchesPlayed(dto.getMatchesPlayed());
        entity.setTotalGoals(dto.getTotalGoals());
        entity.setTotalAssists(dto.getTotalAssists());
        entity.setCurrentRating(dto.getCurrentRating());
        entity.setSpeedRating(dto.getSpeedRating());
        entity.setStaminaRating(dto.getStaminaRating());
        entity.setShootingRating(dto.getShootingRating());
        entity.setPassingRating(dto.getPassingRating());

        // La relation avec l'équipe doit être gérée au niveau du service
    }

    /**
     * Convertit une liste d'entités en liste de DTOs
     */
    public List<PlayerDTO> toDTOList(List<PlayerEntity> entities) {
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
    public List<PlayerEntity> toEntityList(List<PlayerDTO> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }
}