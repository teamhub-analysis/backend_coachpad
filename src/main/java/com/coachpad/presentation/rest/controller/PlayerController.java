package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.PlayerUseCase;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
import com.coachpad.domain.model.PlayerModel;
import com.coachpad.infrastructure.service.FileStorageService;

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

    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerUseCase.getAllPlayers().stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable("id") Long id) {
        return playerUseCase.getPlayerById(id)
                .map(playerDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PlayerDTO> getPlayerByEmail(@PathVariable("email") String email) {
        return playerUseCase.getPlayerByEmail(email)
                .map(playerDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerUseCase.getPlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/active")
    public ResponseEntity<List<PlayerDTO>> getActivePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerUseCase.getActivePlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/number/{number}")
    public ResponseEntity<PlayerDTO> getPlayerByNumberAndTeamId(
            @PathVariable("teamId") Long teamId,
            @PathVariable("number") Integer number) {
        return playerUseCase.getPlayerByNumberAndTeamId(number, teamId)
                .map(playerDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByPosition(@PathVariable("position") String position) {
        List<PlayerDTO> players = playerUseCase.getPlayersByPosition(position).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamIdAndPosition(
            @PathVariable("teamId") Long teamId,
            @PathVariable("position") String position) {
        List<PlayerDTO> players = playerUseCase.getPlayersByTeamIdAndPosition(teamId, position).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerDTO>> searchPlayersByName(@RequestParam("name") String name) {
        List<PlayerDTO> players = playerUseCase.searchPlayersByName(name).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorersByTeamId(
            @PathVariable("teamId") Long teamId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerUseCase.getTopScorersByTeamId(teamId, limit).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/top-assisters")
    public ResponseEntity<List<PlayerDTO>> getTopAssistersByTeamId(
            @PathVariable("teamId") Long teamId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerUseCase.getTopAssistersByTeamId(teamId, limit).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/injured")
    public ResponseEntity<List<PlayerDTO>> getInjuredPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerUseCase.getInjuredPlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/suspended")
    public ResponseEntity<List<PlayerDTO>> getSuspendedPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerUseCase.getSuspendedPlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/available")
    public ResponseEntity<List<PlayerDTO>> getAvailablePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerUseCase.getAvailablePlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByTeamId(@PathVariable("teamId") Long teamId) {
        Double averageRating = playerUseCase.getAverageRatingByTeamId(teamId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/team/{teamId}/count")
    public ResponseEntity<Long> countPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        long count = playerUseCase.countPlayersByTeamId(teamId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam("email") String email) {
        boolean exists = playerUseCase.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/team/{teamId}/number-exists")
    public ResponseEntity<Boolean> numberExistsInTeam(
            @PathVariable("teamId") Long teamId,
            @RequestParam("number") Integer number) {
        boolean exists = playerUseCase.numberExistsInTeam(number, teamId);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerModel model = playerDTOMapper.toModel(playerDTO);
            PlayerModel created = playerUseCase.createPlayer(model);
            return ResponseEntity.status(HttpStatus.CREATED).body(playerDTOMapper.toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
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

    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<Void> deletePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        playerUseCase.deletePlayersByTeamId(teamId);
        return ResponseEntity.noContent().build();
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
