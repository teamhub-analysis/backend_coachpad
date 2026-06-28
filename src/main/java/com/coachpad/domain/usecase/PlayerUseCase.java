package com.coachpad.domain.usecase;

import com.coachpad.domain.model.PlayerModel;

import java.util.List;
import java.util.Optional;

public interface PlayerUseCase {

    List<PlayerModel> getAllPlayers();

    Optional<PlayerModel> getPlayerById(Long id);

    Optional<PlayerModel> getPlayerByEmail(String email);

    List<PlayerModel> getPlayersByTeamId(Long teamId);

    List<PlayerModel> getActivePlayersByTeamId(Long teamId);

    Optional<PlayerModel> getPlayerByNumberAndTeamId(Integer number, Long teamId);

    List<PlayerModel> getPlayersByPosition(String position);

    List<PlayerModel> getPlayersByTeamIdAndPosition(Long teamId, String position);

    List<PlayerModel> searchPlayersByName(String name);

    List<PlayerModel> getTopScorersByTeamId(Long teamId, int limit);

    List<PlayerModel> getTopAssistersByTeamId(Long teamId, int limit);

    List<PlayerModel> getInjuredPlayersByTeamId(Long teamId);

    List<PlayerModel> getSuspendedPlayersByTeamId(Long teamId);

    List<PlayerModel> getAvailablePlayersByTeamId(Long teamId);

    Double getAverageRatingByTeamId(Long teamId);

    long countPlayersByTeamId(Long teamId);

    boolean emailExists(String email);

    boolean numberExistsInTeam(Integer number, Long teamId);

    PlayerModel createPlayer(PlayerModel player);

    PlayerModel updatePlayer(Long id, PlayerModel player);

    void deletePlayer(Long id);

    void deletePlayersByTeamId(Long teamId);

    PlayerModel updatePlayerPhoto(Long id, String photoUrl);
}
