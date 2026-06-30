package com.coachpad.infrastructure.service.impl;

import com.coachpad.domain.model.PlayerModel;
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
    public TeamModel replaceTeamData(Long id, TeamModel team) {
        return teamRepository.replaceTeamData(id, team);
    }

    @Override
    public TeamModel updateTeam(Long id, TeamModel team) {
        return teamRepository.updateTeam(id, team);
    }

    @Override
    public TeamModel updateTeamLogo(Long teamId, String photoUrl) {
        teamDesignRepository.updateTeamLogo(teamId, photoUrl);
        return teamRepository.getTeamById(teamId)
                .orElseThrow(() -> new RuntimeException("Team not found: " + teamId));
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
}
