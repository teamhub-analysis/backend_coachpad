package com.coachpad.domain.repository;

import com.coachpad.domain.model.TeamModel;
import java.util.List;
import java.util.Optional;

public interface TeamRepository {

    List<TeamModel> getAllTeams();

    Optional<TeamModel> getTeamById(Long id);

    Optional<TeamModel> getTeamByName(String name);

    List<TeamModel> searchTeamsByName(String name);

    List<TeamModel> getTeamsByFormationId(Long formationId);

    Optional<TeamModel> getTeamByHeadCoachId(Long coachId);

    long countTeams();

    boolean teamNameExists(String name);

    TeamModel createTeam(TeamModel teamModel);

    TeamModel updateTeam(Long id, TeamModel teamModel);

    void deleteTeam(Long id);
    TeamModel removeDesignFromTeam(Long teamId);
    void cleanupExcelTeams();
}
