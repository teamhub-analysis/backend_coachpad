package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.PlayerEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.PlayerJpaRepository;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamJpaRepository;
import com.coachpad.domain.repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerPostgresqlAdapter implements PlayerRepository {

    private final PlayerJpaRepository PlayerJpaRepository;
    private final TeamJpaRepository TeamJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerModel> getPlayerById(Long id) {
        return PlayerJpaRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByTeamId(Long teamId) {
        return PlayerJpaRepository.findByTeamIdOrderByNumberAsc(teamId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional
    public PlayerModel createPlayer(PlayerModel playerModel) {
        PlayerEntity entity = toEntity(playerModel);
        if (playerModel.getTeam() != null && playerModel.getTeam().getId() != null) {
            TeamEntity team = TeamJpaRepository.findById(playerModel.getTeam().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Team not found with id: " + playerModel.getTeam().getId()));
            entity.setTeam(team);
        }
        PlayerEntity saved = PlayerJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    @Transactional
    public PlayerModel updatePlayer(Long id, PlayerModel playerModel) {
        PlayerEntity existing = PlayerJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Player not found with id: " + id));

        existing.setFirstName(playerModel.getFirstName());
        existing.setLastName(playerModel.getLastName());
        existing.setFullName(playerModel.getFullName());
        existing.setNumber(playerModel.getNumber());
        existing.setDateOfBirth(playerModel.getDateOfBirth());
        existing.setNationality(playerModel.getNationality());
        existing.setEmail(playerModel.getEmail());
        existing.setPhoneNumber(playerModel.getPhoneNumber());
        existing.setPhotoUrl(playerModel.getPhotoUrl());
        existing.setHeightCm(playerModel.getHeightCm());
        existing.setWeightKg(playerModel.getWeightKg());
        existing.setPreferredFoot(playerModel.getPreferredFoot());
        existing.setMainPosition(playerModel.getMainPosition());
        existing.setSecondaryPositions(playerModel.getSecondaryPositions());
        existing.setStatus(playerModel.getStatus());
        existing.setMatchesPlayed(playerModel.getMatchesPlayed());
        existing.setTotalGoals(playerModel.getTotalGoals());
        existing.setTotalAssists(playerModel.getTotalAssists());
        existing.setCurrentRating(playerModel.getCurrentRating());
        existing.setSpeedRating(playerModel.getSpeedRating());
        existing.setStaminaRating(playerModel.getStaminaRating());
        existing.setShootingRating(playerModel.getShootingRating());
        existing.setPassingRating(playerModel.getPassingRating());

        PlayerEntity updated = PlayerJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    @Transactional
    public void deletePlayer(Long id) {
        if (!PlayerJpaRepository.existsById(id)) {
            throw new IllegalArgumentException("Player not found with id: " + id);
        }
        PlayerJpaRepository.deleteById(id);
    }

    private PlayerModel toModel(PlayerEntity entity) {
        var builder = PlayerModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .number(entity.getNumber())
                .dateOfBirth(entity.getDateOfBirth())
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
                .passingRating(entity.getPassingRating());
        if (entity.getTeam() != null) {
            builder.team(TeamModel.builder().id(entity.getTeam().getId()).build());
        }
        return builder.build();
    }

    private PlayerEntity toEntity(PlayerModel model) {
        return PlayerEntity.builder()
                .id(model.getId())
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .fullName(model.getFullName())
                .number(model.getNumber())
                .dateOfBirth(model.getDateOfBirth())
                .nationality(model.getNationality())
                .email(model.getEmail())
                .phoneNumber(model.getPhoneNumber())
                .photoUrl(model.getPhotoUrl())
                .heightCm(model.getHeightCm())
                .weightKg(model.getWeightKg())
                .preferredFoot(model.getPreferredFoot())
                .mainPosition(model.getMainPosition())
                .secondaryPositions(model.getSecondaryPositions())
                .status(model.getStatus())
                .matchesPlayed(model.getMatchesPlayed())
                .totalGoals(model.getTotalGoals())
                .totalAssists(model.getTotalAssists())
                .currentRating(model.getCurrentRating())
                .speedRating(model.getSpeedRating())
                .staminaRating(model.getStaminaRating())
                .shootingRating(model.getShootingRating())
                .passingRating(model.getPassingRating())
                .build();
    }
}
