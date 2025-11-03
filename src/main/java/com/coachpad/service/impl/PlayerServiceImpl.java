package com.coachpad.service.impl;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.persistence.adapter.PlayerAdapter;
import com.coachpad.service.PlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

    private final PlayerAdapter playerAdapter;

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerAdapter.findAll();
    }

    @Override
    public Optional<PlayerDTO> getPlayerById(Long id) {
        return playerAdapter.findById(id);
    }

    @Override
    public Optional<PlayerDTO> getPlayerByEmail(String email) {
        return playerAdapter.findByEmail(email);
    }

    @Override
    public List<PlayerDTO> getPlayersByTeamId(Long teamId) {
        return playerAdapter.findByTeamId(teamId);
    }

    @Override
    public Optional<PlayerDTO> getPlayerByNumberAndTeamId(Integer number, Long teamId) {
        return playerAdapter.findByNumberAndTeamId(number, teamId);
    }

    @Override
    public List<PlayerDTO> getActivePlayersByTeamId(Long teamId) {
        return playerAdapter.findActivePlayersByTeamId(teamId);
    }

    @Override
    public List<PlayerDTO> getPlayersByPosition(String position) {
        return playerAdapter.findByMainPosition(position);
    }

    @Override
    public List<PlayerDTO> getPlayersByTeamIdAndPosition(Long teamId, String position) {
        return playerAdapter.findByTeamIdAndPosition(teamId, position);
    }

    @Override
    public List<PlayerDTO> searchPlayersByName(String name) {
        return playerAdapter.searchByName(name);
    }

    @Override
    public List<PlayerDTO> getTopScorersByTeamId(Long teamId, int limit) {
        return playerAdapter.findTopScorersByTeamId(teamId, limit);
    }

    @Override
    public List<PlayerDTO> getTopAssistersByTeamId(Long teamId, int limit) {
        return playerAdapter.findTopAssistersByTeamId(teamId, limit);
    }

    @Override
    public List<PlayerDTO> getInjuredPlayersByTeamId(Long teamId) {
        return playerAdapter.findInjuredPlayersByTeamId(teamId);
    }

    @Override
    public List<PlayerDTO> getSuspendedPlayersByTeamId(Long teamId) {
        return playerAdapter.findSuspendedPlayersByTeamId(teamId);
    }

    @Override
    public List<PlayerDTO> getAvailablePlayersByTeamId(Long teamId) {
        return playerAdapter.findAvailablePlayersByTeamId(teamId);
    }

    @Override
    public Double getAverageRatingByTeamId(Long teamId) {
        return playerAdapter.calculateAverageRatingByTeamId(teamId);
    }

    @Override
    public long countPlayersByTeamId(Long teamId) {
        return playerAdapter.countByTeamId(teamId);
    }

    @Override
    public boolean emailExists(String email) {
        return playerAdapter.existsByEmail(email);
    }

    @Override
    public boolean numberExistsInTeam(Integer number, Long teamId) {
        return playerAdapter.existsByNumberAndTeamId(number, teamId);
    }

    @Override
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        return playerAdapter.create(playerDTO);
    }

    @Override
    public PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO) {
        return playerAdapter.update(id, playerDTO);
    }

    @Override
    public void deletePlayer(Long id) {
        playerAdapter.delete(id);
    }

    @Override
    public void deletePlayersByTeamId(Long teamId) {
        playerAdapter.deleteByTeamId(teamId);
    }
}
