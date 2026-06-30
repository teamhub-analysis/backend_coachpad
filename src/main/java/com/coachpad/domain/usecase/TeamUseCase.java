package com.coachpad.domain.usecase;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamModel;

import java.util.List;
import java.util.Optional;

public interface TeamUseCase {

    List<TeamModel> getAllTeams();

    Optional<TeamModel> getTeamById(Long id);

    TeamModel updateTeam(Long id, TeamModel team);

    TeamModel replaceTeamData(Long id, TeamModel team);

    TeamModel updateTeamLogo(Long teamId, String photoUrl);

    PlayerModel addPlayerToTeam(Long teamId, PlayerModel player);
}
