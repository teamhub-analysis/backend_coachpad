package com.coachpad.infrastructure.service.impl;

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
    public Optional<PlayerModel> getPlayerById(Long id) {
        return playerRepository.getPlayerById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlayerModel> getPlayersByTeamId(Long teamId) {
        return playerRepository.getPlayersByTeamId(teamId);
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
    public PlayerModel updatePlayerPhoto(Long id, String photoUrl) {
        return playerRepository.getPlayerById(id)
                .map(player -> {
                    player.setPhotoUrl(photoUrl);
                    return playerRepository.updatePlayer(id, player);
                })
                .orElseThrow(() -> new IllegalArgumentException("Player not found: " + id));
    }
}
