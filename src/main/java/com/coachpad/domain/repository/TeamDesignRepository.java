package com.coachpad.domain.repository;

import com.coachpad.domain.model.TeamDesignModel;

public interface TeamDesignRepository {

    TeamDesignModel updateTeamLogo(Long teamId, String photoUrl);
}
