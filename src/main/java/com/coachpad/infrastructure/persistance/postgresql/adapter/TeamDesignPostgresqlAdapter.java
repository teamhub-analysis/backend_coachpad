package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.TeamDesignModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamDesignEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamDesignJpaRepository;
import com.coachpad.domain.repository.TeamDesignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class TeamDesignPostgresqlAdapter implements TeamDesignRepository {

    private final TeamDesignJpaRepository TeamDesignJpaRepository;

    @Override
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
}
