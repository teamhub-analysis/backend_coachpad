package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.*;
import com.coachpad.infrastructure.persistance.postgresql.entity.*;
import com.coachpad.infrastructure.persistance.postgresql.mapper.CoachEntityMapper;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamJpaRepository;
import com.coachpad.domain.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamPostgresqlAdapter implements TeamRepository {

    private final TeamJpaRepository TeamJpaRepository;
    private final CoachEntityMapper coachEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> getAllTeams() {
        return TeamJpaRepository.findAll()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamById(Long id) {
        return TeamJpaRepository.findWithAllRelationsById(id).map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamByName(String name) {
        return TeamJpaRepository.findByName(name).map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> searchTeamsByName(String name) {
        return TeamJpaRepository.searchByName(name)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> getTeamsByFormationId(Long formationId) {
        return TeamJpaRepository.findByFormationId(formationId)
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamByHeadCoachId(Long coachId) {
        return TeamJpaRepository.findByHeadCoachId(coachId).map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeams() {
        return TeamJpaRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean teamNameExists(String name) {
        return TeamJpaRepository.existsByName(name);
    }

    @Override
    @Transactional
    public TeamModel createTeam(TeamModel teamModel) {
        TeamEntity entity = toEntity(teamModel);
        TeamEntity saved = TeamJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    @Transactional
    public TeamModel updateTeam(Long id, TeamModel teamModel) {
        TeamEntity existing = TeamJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
        existing.setName(teamModel.getName());
        existing.setNickname(teamModel.getNickname());
        existing.setAgeCategory(teamModel.getAgeCategory());
        TeamEntity updated = TeamJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    @Transactional
    public void deleteTeam(Long id) {
        TeamJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TeamModel removeDesignFromTeam(Long teamId) {
        TeamEntity existing = TeamJpaRepository.findById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + teamId));
        existing.setDesign(null);
        TeamEntity updated = TeamJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    @Transactional
    public void cleanupExcelTeams() {
        List<TeamEntity> excelTeams = TeamJpaRepository.findBySource("EXCEL");
        TeamJpaRepository.deleteAll(excelTeams);
    }

    private TeamModel toModel(TeamEntity entity) {
        var builder = TeamModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nickname(entity.getNickname())
                .ageCategory(entity.getAgeCategory())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt());

        if (entity.getFormation() != null) {
            builder.formation(FormationModel.builder()
                    .id(entity.getFormation().getId())
                    .orderedPositions(entity.getFormation().getOrderedPositions())
                    .build());
        }

        if (entity.getDesign() != null) {
            TeamDesignEntity d = entity.getDesign();
            TeamDesignModel designModel = TeamDesignModel.builder()
                    .id(d.getId())
                    .style(d.getStyle())
                    .logoFilePath(d.getLogoFilePath())
                    .logoIconName(d.getLogoIconName())
                    .usePlayerPhotos(d.getUsePlayerPhotos() != null && d.getUsePlayerPhotos())
                    .jerseyDesign(d.getJerseyDesign())
                    .build();
            if (d.getColors() != null) {
                designModel.setColors(TeamKitColorsModel.builder()
                        .id(d.getColors().getId())
                        .primaryHex(d.getColors().getPrimaryHex())
                        .secondaryHex(d.getColors().getSecondaryHex())
                        .trimHex(d.getColors().getTrimHex())
                        .build());
            }
            builder.design(designModel);
        }

        if (entity.getCoaches() != null) {
            builder.coaches(entity.getCoaches().stream()
                    .map(coachEntityMapper::toModel)
                    .toList());
        }

        if (entity.getMedicalStaff() != null) {
            builder.medicalStaff(entity.getMedicalStaff().stream()
                    .map(coachEntityMapper::toModel)
                    .toList());
        }

        if (entity.getPlayers() != null) {
            builder.players(entity.getPlayers().stream()
                    .map(this::toPlayerModel)
                    .toList());
        }

        if (entity.getGroups() != null) {
            builder.groups(entity.getGroups().stream()
                    .map(this::toSquadGroupModel)
                    .toList());
        }

        return builder.build();
    }

    private PlayerModel toPlayerModel(PlayerEntity entity) {
        return PlayerModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .number(entity.getNumber())
                .dateOfBirth(entity.getDateOfBirth())
                .nationality(entity.getNationality())
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
                .build();
    }

    private SquadGroupModel toSquadGroupModel(SquadGroupEntity entity) {
        return SquadGroupModel.builder()
                .id(entity.getId())
                .name(entity.getName())
                .teamId(entity.getTeam() != null ? entity.getTeam().getId() : null)
                .colorHex(entity.getColorHex())
                .playerIds(entity.getPlayers() != null ? entity.getPlayers().stream().map(PlayerEntity::getId).toList() : null)
                .isVisible(entity.isVisible())
                .isMainGroup(entity.isMainGroup())
                .build();
    }

    private TeamEntity toEntity(TeamModel model) {
        TeamEntity.TeamEntityBuilder builder = TeamEntity.builder()
                .id(model.getId())
                .name(model.getName())
                .nickname(model.getNickname())
                .ageCategory(model.getAgeCategory());

        if (model.getFormation() != null && model.getFormation().getId() != null) {
            builder.formation(FormationEntity.builder()
                    .id(model.getFormation().getId())
                    .build());
        }

        return builder.build();
    }
}
