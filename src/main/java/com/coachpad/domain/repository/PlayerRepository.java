package com.coachpad.domain.repository;

import com.coachpad.domain.model.PlayerModel;
import java.util.List;
import java.util.Optional;

public interface PlayerRepository {

    Optional<PlayerModel> getPlayerById(Long id);

    List<PlayerModel> getPlayersByTeamId(Long teamId);

    PlayerModel createPlayer(PlayerModel playerModel);

    PlayerModel updatePlayer(Long id, PlayerModel playerModel);

    void deletePlayer(Long id);
}
