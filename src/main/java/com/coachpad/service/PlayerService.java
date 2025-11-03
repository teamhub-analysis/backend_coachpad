package com.coachpad.service;

import com.coachpad.dto.PlayerDTO;
import java.util.List;
import java.util.Optional;

public interface PlayerService {

    List<PlayerDTO> getAllPlayers();
    Optional<PlayerDTO> getPlayerById(Long id);
    Optional<PlayerDTO> getPlayerByEmail(String email);
    List<PlayerDTO> getPlayersByTeamId(Long teamId);
    Optional<PlayerDTO> getPlayerByNumberAndTeamId(Integer number, Long teamId);
    List<PlayerDTO> getActivePlayersByTeamId(Long teamId);
    List<PlayerDTO> getPlayersByPosition(String position);
    List<PlayerDTO> getPlayersByTeamIdAndPosition(Long teamId, String position);
    List<PlayerDTO> searchPlayersByName(String name);
    List<PlayerDTO> getTopScorersByTeamId(Long teamId, int limit);
    List<PlayerDTO> getTopAssistersByTeamId(Long teamId, int limit);
    List<PlayerDTO> getInjuredPlayersByTeamId(Long teamId);
    List<PlayerDTO> getSuspendedPlayersByTeamId(Long teamId);
    List<PlayerDTO> getAvailablePlayersByTeamId(Long teamId);
    Double getAverageRatingByTeamId(Long teamId);
    long countPlayersByTeamId(Long teamId);
    boolean emailExists(String email);
    boolean numberExistsInTeam(Integer number, Long teamId);
    PlayerDTO createPlayer(PlayerDTO playerDTO);
    PlayerDTO updatePlayer(Long id, PlayerDTO playerDTO);
    void deletePlayer(Long id);
    void deletePlayersByTeamId(Long teamId);
}
