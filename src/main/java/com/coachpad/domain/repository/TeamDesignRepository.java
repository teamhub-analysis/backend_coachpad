package com.coachpad.domain.repository;

import com.coachpad.domain.model.TeamDesignModel;

import java.util.List;
import java.util.Optional;

public interface TeamDesignRepository {
    List<TeamDesignModel> getAllDesigns();
    Optional<TeamDesignModel> getDesignById(Long id);
    Optional<TeamDesignModel> getDesignByTeamId(Long teamId);
    List<TeamDesignModel> getDesignsByStyle(String style);
    List<TeamDesignModel> getDesignsByJerseyDesign(String jerseyDesign);
    TeamDesignModel createDesign(TeamDesignModel model);
    TeamDesignModel updateDesign(Long id, TeamDesignModel model);
    void deleteDesign(Long id);
    TeamDesignModel updateTeamLogo(Long teamId, String photoUrl);
}
