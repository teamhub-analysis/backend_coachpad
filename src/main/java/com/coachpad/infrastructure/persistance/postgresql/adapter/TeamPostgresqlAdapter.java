package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.*;
import com.coachpad.infrastructure.persistance.postgresql.entity.*;
import com.coachpad.infrastructure.persistance.postgresql.mapper.CoachEntityMapper;
import com.coachpad.infrastructure.persistance.postgresql.repository.FormationJpaRepository;
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
    private final FormationJpaRepository FormationJpaRepository;
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
    @Transactional
    public TeamModel replaceTeamData(Long id, TeamModel teamModel) {
        TeamEntity existing = TeamJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));

        existing.setName(teamModel.getName());
        existing.setNickname(teamModel.getNickname());
        existing.setAgeCategory(teamModel.getAgeCategory());

        if (teamModel.getFormation() != null && teamModel.getFormation().getId() != null) {
            FormationEntity formation = FormationJpaRepository.findById(teamModel.getFormation().getId())
                    .orElseThrow(() -> new RuntimeException("Formation not found with id: " + teamModel.getFormation().getId()));
            existing.setFormation(formation);
        }

        existing.getPlayers().clear();
        existing.getCoaches().clear();
        existing.getMedicalStaff().clear();
        TeamJpaRepository.flush();

        if (teamModel.getPlayers() != null) {
            teamModel.getPlayers().forEach(pm -> {
                PlayerEntity pe = toPlayerEntity(pm);
                pe.setTeam(existing);
                existing.getPlayers().add(pe);
            });
        }

        if (teamModel.getCoaches() != null) {
            teamModel.getCoaches().forEach(cm -> {
                CoachEntity ce = coachEntityMapper.toEntity(cm);
                ce.setTeam(existing);
                existing.getCoaches().add(ce);
            });
        }

        if (teamModel.getMedicalStaff() != null) {
            teamModel.getMedicalStaff().forEach(cm -> {
                CoachEntity ce = coachEntityMapper.toEntity(cm);
                ce.setTeam(existing);
                existing.getMedicalStaff().add(ce);
            });
        }

        if (teamModel.getDesign() != null) {
            TeamDesignEntity de = existing.getDesign();
            if (de == null) {
                de = new TeamDesignEntity();
                de.setTeam(existing);
                existing.setDesign(de);
            }
            TeamDesignModel dm = teamModel.getDesign();
            de.setStyle(dm.getStyle());
            de.setLogoFilePath(dm.getLogoFilePath());
            de.setLogoIconName(dm.getLogoIconName());
            de.setJerseyDesign(dm.getJerseyDesign());
            de.setUsePlayerPhotos(dm.isUsePlayerPhotos());
            if (dm.getColors() != null) {
                TeamKitColorsEntity ke = de.getColors();
                if (ke == null) {
                    ke = new TeamKitColorsEntity();
                    de.setColors(ke);
                }
                ke.setPrimaryHex(dm.getColors().getPrimaryHex());
                ke.setSecondaryHex(dm.getColors().getSecondaryHex());
                ke.setTrimHex(dm.getColors().getTrimHex());
            }
        }

        TeamEntity updated = TeamJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    @Transactional
    public TeamModel updateTeam(Long id, TeamModel teamModel) {
        TeamEntity existing = TeamJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found with id: " + id));
        existing.setName(teamModel.getName());
        existing.setNickname(teamModel.getNickname());
        existing.setAgeCategory(teamModel.getAgeCategory());
        if (teamModel.getFormation() != null && teamModel.getFormation().getId() != null) {
            FormationEntity formation = FormationJpaRepository.findById(teamModel.getFormation().getId())
                    .orElseThrow(() -> new RuntimeException("Formation not found with id: " + teamModel.getFormation().getId()));
            existing.setFormation(formation);
        }
        TeamEntity updated = TeamJpaRepository.save(existing);
        return toModel(updated);
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

    private PlayerEntity toPlayerEntity(PlayerModel model) {
        return PlayerEntity.builder()
                .firstName(model.getFirstName())
                .lastName(model.getLastName())
                .fullName(model.getFullName())
                .number(model.getNumber())
                .dateOfBirth(model.getDateOfBirth())
                .nationality(model.getNationality())
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
}
