package com.coachpad.infrastructure.service;

import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.repository.PlayerRepository;
import com.coachpad.domain.usecase.PlayerUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PlayerUseCaseImpl implements PlayerUseCase {

    private final PlayerRepository playerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getAllPlayers() {
        return playerRepository.getAllPlayers();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerModel> getPlayerById(Long id) {
        return playerRepository.getPlayerById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerModel> getPlayerByEmail(String email) {
        return playerRepository.getPlayerByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByTeamId(Long teamId) {
        return playerRepository.getPlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getActivePlayersByTeamId(Long teamId) {
        return playerRepository.getActivePlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PlayerModel> getPlayerByNumberAndTeamId(Integer number, Long teamId) {
        return playerRepository.getPlayerByNumberAndTeamId(number, teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByPosition(String position) {
        return playerRepository.getPlayersByPosition(position);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByTeamIdAndPosition(Long teamId, String position) {
        return playerRepository.getPlayersByTeamIdAndPosition(teamId, position);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> searchPlayersByName(String name) {
        return playerRepository.searchPlayersByName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getTopScorersByTeamId(Long teamId, int limit) {
        return playerRepository.getTopScorersByTeamId(teamId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getTopAssistersByTeamId(Long teamId, int limit) {
        return playerRepository.getTopAssistersByTeamId(teamId, limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getInjuredPlayersByTeamId(Long teamId) {
        return playerRepository.getInjuredPlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getSuspendedPlayersByTeamId(Long teamId) {
        return playerRepository.getSuspendedPlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getAvailablePlayersByTeamId(Long teamId) {
        return playerRepository.getAvailablePlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public Double getAverageRatingByTeamId(Long teamId) {
        return playerRepository.getAverageRatingByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPlayersByTeamId(Long teamId) {
        return playerRepository.countPlayersByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return playerRepository.emailExists(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean numberExistsInTeam(Integer number, Long teamId) {
        return playerRepository.numberExistsInTeam(number, teamId);
    }

    @Override
    public PlayerModel createPlayer(PlayerModel player) {
        return playerRepository.createPlayer(player);
    }

    @Override
    public PlayerModel updatePlayer(Long id, PlayerModel player) {
        return playerRepository.updatePlayer(id, player);
    }

    @Override
    public void deletePlayer(Long id) {
        playerRepository.deletePlayer(id);
    }

    @Override
    public void deletePlayersByTeamId(Long teamId) {
        playerRepository.deletePlayersByTeamId(teamId);
    }

    @Override
    public PlayerModel updatePlayerPhoto(Long id, String photoUrl) {
        return playerRepository.getPlayerById(id)
                .map(player -> {
                    player.setPhotoUrl(photoUrl);
                    return playerRepository.updatePlayer(id, player);
                })
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + id));
    }
}
