package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.TeamDesignModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamDesignEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamDesignJpaRepository;
import com.coachpad.domain.repository.TeamDesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Transactional
public class TeamDesignPostgresqlAdapter implements TeamDesignRepository {

    private final TeamDesignJpaRepository TeamDesignJpaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignModel> getAllDesigns() {
        return TeamDesignJpaRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignModel> getDesignById(Long id) {
        return TeamDesignJpaRepository.findById(id)
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignModel> getDesignByTeamId(Long teamId) {
        return TeamDesignJpaRepository.findByTeamId(teamId)
                .map(this::toModel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignModel> getDesignsByStyle(String style) {
        try {
            com.coachpad.domain.model.enums.WidgetAppearance designStyle =
                com.coachpad.domain.model.enums.WidgetAppearance.valueOf(style.toUpperCase());
            return TeamDesignJpaRepository.findByStyle(designStyle)
                    .stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Style invalide : " + style);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignModel> getDesignsByJerseyDesign(String jerseyDesign) {
        try {
            com.coachpad.domain.model.enums.JerseyDesign jersey =
                com.coachpad.domain.model.enums.JerseyDesign.valueOf(jerseyDesign.toUpperCase());
            return TeamDesignJpaRepository.findByJerseyDesign(jersey)
                    .stream()
                    .map(this::toModel)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Jersey design invalide : " + jerseyDesign);
        }
    }

    @Override
    public TeamDesignModel createDesign(TeamDesignModel model) {
        TeamDesignEntity entity = toEntity(model);
        TeamDesignEntity saved = TeamDesignJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public TeamDesignModel updateDesign(Long id, TeamDesignModel model) {
        TeamDesignEntity existing = TeamDesignJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Design non trouvÃ© avec id : " + id));

        existing.setStyle(model.getStyle());
        existing.setLogoFilePath(model.getLogoFilePath());
        existing.setLogoIconName(model.getLogoIconName());
        existing.setJerseyDesign(model.getJerseyDesign());

        TeamDesignEntity updated = TeamDesignJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    public void deleteDesign(Long id) {
        if (!TeamDesignJpaRepository.existsById(id)) {
            throw new RuntimeException("Design non trouvÃ© avec id : " + id);
        }
        TeamDesignJpaRepository.deleteById(id);
    }

    @Override
    @Transactional
    public TeamDesignModel updateTeamLogo(Long teamId, String photoUrl) {
        TeamDesignEntity existing = TeamDesignJpaRepository.findByTeamId(teamId)
                .orElseThrow(() -> new RuntimeException("TeamDesign not found for team: " + teamId));
        existing.setLogoFilePath(photoUrl);
        TeamDesignEntity saved = TeamDesignJpaRepository.save(existing);
        return toModel(saved);
    }

    private TeamDesignModel toModel(TeamDesignEntity entity) {
        return TeamDesignModel.builder()
                .id(entity.getId())
                .style(entity.getStyle())
                .logoFilePath(entity.getLogoFilePath())
                .logoIconName(entity.getLogoIconName())
                .usePlayerPhotos(entity.getUsePlayerPhotos() != null && entity.getUsePlayerPhotos())
                .jerseyDesign(entity.getJerseyDesign())
                .build();
    }

    private TeamDesignEntity toEntity(TeamDesignModel model) {
        return TeamDesignEntity.builder()
                .id(model.getId())
                .style(model.getStyle())
                .logoFilePath(model.getLogoFilePath())
                .logoIconName(model.getLogoIconName())
                .usePlayerPhotos(model.isUsePlayerPhotos())
                .jerseyDesign(model.getJerseyDesign())
                .build();
    }
}
