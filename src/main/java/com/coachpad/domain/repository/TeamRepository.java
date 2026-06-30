package com.coachpad.domain.repository;

import com.coachpad.domain.model.TeamModel;
import java.util.List;
import java.util.Optional;

public interface TeamRepository {

    List<TeamModel> getAllTeams();

    Optional<TeamModel> getTeamById(Long id);

    TeamModel updateTeam(Long id, TeamModel teamModel);

    TeamModel replaceTeamData(Long id, TeamModel teamModel);
}
