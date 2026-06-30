package com.coachpad.domain.usecase;

import com.coachpad.domain.model.PlayerModel;

import java.util.List;
import java.util.Optional;

public interface PlayerUseCase {

    Optional<PlayerModel> getPlayerById(Long id);

    List<PlayerModel> getPlayersByTeamId(Long teamId);

    PlayerModel updatePlayer(Long id, PlayerModel player);

    void deletePlayer(Long id);

    PlayerModel updatePlayerPhoto(Long id, String photoUrl);
}
