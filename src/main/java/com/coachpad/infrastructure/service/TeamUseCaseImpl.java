package com.coachpad.infrastructure.service;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamDesignModel;
import com.coachpad.domain.model.TeamModel;
import com.coachpad.domain.repository.PlayerRepository;
import com.coachpad.domain.repository.TeamDesignRepository;
import com.coachpad.domain.repository.TeamRepository;
import com.coachpad.domain.usecase.TeamUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamUseCaseImpl implements TeamUseCase {

    private final TeamRepository teamRepository;
    private final TeamDesignRepository teamDesignRepository;
    private final PlayerRepository playerRepository;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> getAllTeams() {
        return teamRepository.getAllTeams();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamById(Long id) {
        return teamRepository.getTeamById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamByName(String name) {
        return teamRepository.getTeamByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> searchTeamsByName(String name) {
        return teamRepository.searchTeamsByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamModel> getTeamsByFormationId(Long formationId) {
        return teamRepository.getTeamsByFormationId(formationId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamModel> getTeamByHeadCoachId(Long coachId) {
        return teamRepository.getTeamByHeadCoachId(coachId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeams() {
        return teamRepository.countTeams();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean teamNameExists(String name) {
        return teamRepository.teamNameExists(name);
    }

    @Override
    public TeamModel createTeam(TeamModel team) {
        return teamRepository.createTeam(team);
    }

    @Override
    public TeamModel updateTeam(Long id, TeamModel team) {
        return teamRepository.updateTeam(id, team);
    }

    @Override
    public void deleteTeam(Long id) {
        teamRepository.deleteTeam(id);
    }

    @Override
    public void cleanupExcelTeams() {
        teamRepository.cleanupExcelTeams();
    }

    @Override
    public TeamModel removeDesignFromTeam(Long teamId) {
        return teamRepository.removeDesignFromTeam(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignModel> getTeamDesign(Long teamId) {
        return teamDesignRepository.getDesignByTeamId(teamId);
    }

    @Override
    public TeamDesignModel createTeamDesign(TeamDesignModel design) {
        return teamDesignRepository.createDesign(design);
    }

    @Override
    public TeamDesignModel updateTeamDesign(Long designId, TeamDesignModel design) {
        return teamDesignRepository.updateDesign(designId, design);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByTeamId(Long teamId) {
        return playerRepository.getPlayersByTeamId(teamId);
    }

    @Override
    public PlayerModel addPlayerToTeam(Long teamId, PlayerModel player) {
        return teamRepository.getTeamById(teamId)
                .map(team -> {
                    player.setTeam(team);
                    return playerRepository.createPlayer(player);
                })
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
    }

    @Override
    public List<PlayerModel> addPlayersToTeam(Long teamId, List<PlayerModel> players) {
        TeamModel team = teamRepository.getTeamById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));
        return players.stream()
                .peek(p -> p.setTeam(team))
                .map(playerRepository::createPlayer)
                .toList();
    }

    @Override
    public void deletePlayersByTeamId(Long teamId) {
        playerRepository.deletePlayersByTeamId(teamId);
    }

    @Override
    public TeamDesignModel updateTeamLogo(Long teamId, String photoUrl) {
        return teamDesignRepository.updateTeamLogo(teamId, photoUrl);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignModel> getAllTeamDesigns() {
        return teamDesignRepository.getAllDesigns();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignModel> getTeamDesignById(Long id) {
        return teamDesignRepository.getDesignById(id);
    }
}
