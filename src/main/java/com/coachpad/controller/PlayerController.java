package com.coachpad.controller;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.service.PlayerService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlayerController {

    private final PlayerService playerService;

    // =================================================================
    // MÉTHODES DE LECTURE (GET)
    // =================================================================
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable("id") Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<PlayerDTO> getPlayerByEmail(@PathVariable("email") String email) {
        return playerService.getPlayerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerService.getPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/active")
    public ResponseEntity<List<PlayerDTO>> getActivePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerService.getActivePlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/number/{number}")
    public ResponseEntity<PlayerDTO> getPlayerByNumberAndTeamId(
            @PathVariable("teamId") Long teamId,
            @PathVariable("number") Integer number) {
        return playerService.getPlayerByNumberAndTeamId(number, teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByPosition(@PathVariable("position") String position) {
        List<PlayerDTO> players = playerService.getPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamIdAndPosition(
            @PathVariable("teamId") Long teamId,
            @PathVariable("position") String position) {
        List<PlayerDTO> players = playerService.getPlayersByTeamIdAndPosition(teamId, position);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PlayerDTO>> searchPlayersByName(@RequestParam("name") String name) {
        List<PlayerDTO> players = playerService.searchPlayersByName(name);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorersByTeamId(
            @PathVariable("teamId") Long teamId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerService.getTopScorersByTeamId(teamId, limit);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/top-assisters")
    public ResponseEntity<List<PlayerDTO>> getTopAssistersByTeamId(
            @PathVariable("teamId") Long teamId,
            @RequestParam(value = "limit", defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerService.getTopAssistersByTeamId(teamId, limit);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/injured")
    public ResponseEntity<List<PlayerDTO>> getInjuredPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerService.getInjuredPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/suspended")
    public ResponseEntity<List<PlayerDTO>> getSuspendedPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerService.getSuspendedPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/available")
    public ResponseEntity<List<PlayerDTO>> getAvailablePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        List<PlayerDTO> players = playerService.getAvailablePlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    @GetMapping("/team/{teamId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByTeamId(@PathVariable("teamId") Long teamId) {
        Double averageRating = playerService.getAverageRatingByTeamId(teamId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/team/{teamId}/count")
    public ResponseEntity<Long> countPlayersByTeamId(@PathVariable("teamId") Long teamId) {
        long count = playerService.countPlayersByTeamId(teamId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam("email") String email) {
        boolean exists = playerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/team/{teamId}/number-exists")
    public ResponseEntity<Boolean> numberExistsInTeam(
            @PathVariable("teamId") Long teamId,
            @RequestParam("number") Integer number) {
        boolean exists = playerService.numberExistsInTeam(number, teamId);
        return ResponseEntity.ok(exists);
    }

    // =================================================================
    // MÉTHODES D'ÉCRITURE (POST, PUT, DELETE)
    // =================================================================

    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO createdPlayer = playerService.createPlayer(playerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/teams/{teamId}/players")
    public ResponseEntity<PlayerDTO> addPlayerToTeam(
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody PlayerDTO playerDTO) {

        try {
            PlayerDTO createdPlayer = playerService.createPlayerForTeam(teamId, playerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/teams/{teamId}/bulk")
    public ResponseEntity<List<PlayerDTO>> addPlayersToTeam(
            @PathVariable("teamId") Long teamId,
            @Valid @RequestBody List<PlayerDTO> playerDTOs) {

        try {
            List<PlayerDTO> createdPlayers = playerService.createPlayersForTeam(teamId, playerDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayers);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(
            @PathVariable("id") Long id,
            @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO updatedPlayer = playerService.updatePlayer(id, playerDTO);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable("id") Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<Void> deletePlayersByTeamId(@PathVariable("teamId") Long teamId) {
        playerService.deletePlayersByTeamId(teamId);
        return ResponseEntity.noContent().build();
    }
}
