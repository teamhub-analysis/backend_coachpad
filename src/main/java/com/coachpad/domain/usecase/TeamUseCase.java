package com.coachpad.domain.usecase;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamDesignModel;
import com.coachpad.domain.model.TeamModel;

import java.util.List;
import java.util.Optional;

public interface TeamUseCase {

    List<TeamModel> getAllTeams();

    Optional<TeamModel> getTeamById(Long id);

    Optional<TeamModel> getTeamByName(String name);

    List<TeamModel> searchTeamsByName(String name);

    List<TeamModel> getTeamsByFormationId(Long formationId);

    Optional<TeamModel> getTeamByHeadCoachId(Long coachId);

    long countTeams();

    boolean teamNameExists(String name);

    TeamModel createTeam(TeamModel team);

    TeamModel updateTeam(Long id, TeamModel team);

    void deleteTeam(Long id);

    void cleanupExcelTeams();

    TeamModel removeDesignFromTeam(Long teamId);

    Optional<TeamDesignModel> getTeamDesign(Long teamId);

    TeamDesignModel createTeamDesign(TeamDesignModel design);

    TeamDesignModel updateTeamDesign(Long designId, TeamDesignModel design);

    TeamDesignModel updateTeamLogo(Long teamId, String photoUrl);

    List<TeamDesignModel> getAllTeamDesigns();

    Optional<TeamDesignModel> getTeamDesignById(Long id);

    List<PlayerModel> getPlayersByTeamId(Long teamId);

    PlayerModel addPlayerToTeam(Long teamId, PlayerModel player);

    List<PlayerModel> addPlayersToTeam(Long teamId, List<PlayerModel> players);

    void deletePlayersByTeamId(Long teamId);
}
