package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.PlayerUseCase;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
import com.coachpad.domain.model.PlayerModel;
import com.coachpad.infrastructure.service.storage.FileStorageService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerUseCase playerUseCase;
    private final PlayerDTOMapper playerDTOMapper;
    private final FileStorageService fileStorageService;

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable("id") Long id) {
        return playerUseCase.getPlayerById(id)
                .map(playerDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(
            @PathVariable("teamId") Long teamId,
            @RequestParam(name = "offset", defaultValue = "0") int offset,
            @RequestParam(name = "limit", defaultValue = "200") int limit) {
        List<PlayerDTO> players = playerUseCase.getPlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(
            @PathVariable("id") Long id,
            @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerModel model = playerDTOMapper.toModel(playerDTO);
            PlayerModel updated = playerUseCase.updatePlayer(id, model);
            return ResponseEntity.ok(playerDTOMapper.toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Long id) {
        try {
            playerUseCase.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/photo")
    public ResponseEntity<PlayerDTO> updatePlayerPhoto(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = fileStorageService.storeFile(file, FileStorageService.DIR_IMAGE);
            PlayerModel updated = playerUseCase.updatePlayerPhoto(id, photoUrl);
            return ResponseEntity.ok(playerDTOMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
