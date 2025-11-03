package com.coachpad.controller;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.service.PlayerService;
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

    /**
     * GET /api/players - Récupère tous les joueurs
     */
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        List<PlayerDTO> players = playerService.getAllPlayers();
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/{id} - Récupère un joueur par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/players/email/{email} - Récupère un joueur par email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<PlayerDTO> getPlayerByEmail(@PathVariable String email) {
        return playerService.getPlayerByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/players/team/{teamId} - Récupère tous les joueurs d'une équipe
     */
    @GetMapping("/team/{teamId}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/active - Récupère les joueurs actifs d'une équipe
     */
    @GetMapping("/team/{teamId}/active")
    public ResponseEntity<List<PlayerDTO>> getActivePlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getActivePlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/number/{number} - Récupère un joueur par numéro dans une équipe
     */
    @GetMapping("/team/{teamId}/number/{number}")
    public ResponseEntity<PlayerDTO> getPlayerByNumberAndTeamId(
            @PathVariable Long teamId,
            @PathVariable Integer number) {
        return playerService.getPlayerByNumberAndTeamId(number, teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/players/position/{position} - Récupère tous les joueurs par position
     */
    @GetMapping("/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByPosition(@PathVariable String position) {
        List<PlayerDTO> players = playerService.getPlayersByPosition(position);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/position/{position} - Récupère les joueurs d'une équipe par position
     */
    @GetMapping("/team/{teamId}/position/{position}")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamIdAndPosition(
            @PathVariable Long teamId,
            @PathVariable String position) {
        List<PlayerDTO> players = playerService.getPlayersByTeamIdAndPosition(teamId, position);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/search?name={name} - Recherche des joueurs par nom
     */
    @GetMapping("/search")
    public ResponseEntity<List<PlayerDTO>> searchPlayersByName(@RequestParam String name) {
        List<PlayerDTO> players = playerService.searchPlayersByName(name);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/top-scorers?limit={limit} - Récupère les meilleurs buteurs
     */
    @GetMapping("/team/{teamId}/top-scorers")
    public ResponseEntity<List<PlayerDTO>> getTopScorersByTeamId(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerService.getTopScorersByTeamId(teamId, limit);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/top-assisters?limit={limit} - Récupère les meilleurs passeurs
     */
    @GetMapping("/team/{teamId}/top-assisters")
    public ResponseEntity<List<PlayerDTO>> getTopAssistersByTeamId(
            @PathVariable Long teamId,
            @RequestParam(defaultValue = "10") int limit) {
        List<PlayerDTO> players = playerService.getTopAssistersByTeamId(teamId, limit);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/injured - Récupère les joueurs blessés
     */
    @GetMapping("/team/{teamId}/injured")
    public ResponseEntity<List<PlayerDTO>> getInjuredPlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getInjuredPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/suspended - Récupère les joueurs suspendus
     */
    @GetMapping("/team/{teamId}/suspended")
    public ResponseEntity<List<PlayerDTO>> getSuspendedPlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getSuspendedPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/available - Récupère les joueurs disponibles
     */
    @GetMapping("/team/{teamId}/available")
    public ResponseEntity<List<PlayerDTO>> getAvailablePlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getAvailablePlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * GET /api/players/team/{teamId}/average-rating - Calcule la moyenne des notes
     */
    @GetMapping("/team/{teamId}/average-rating")
    public ResponseEntity<Double> getAverageRatingByTeamId(@PathVariable Long teamId) {
        Double averageRating = playerService.getAverageRatingByTeamId(teamId);
        return ResponseEntity.ok(averageRating);
    }

    /**
     * GET /api/players/team/{teamId}/count - Compte les joueurs d'une équipe
     */
    @GetMapping("/team/{teamId}/count")
    public ResponseEntity<Long> countPlayersByTeamId(@PathVariable Long teamId) {
        long count = playerService.countPlayersByTeamId(teamId);
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/players/email-exists?email={email} - Vérifie si un email existe
     */
    @GetMapping("/email-exists")
    public ResponseEntity<Boolean> emailExists(@RequestParam String email) {
        boolean exists = playerService.emailExists(email);
        return ResponseEntity.ok(exists);
    }

    /**
     * GET /api/players/team/{teamId}/number-exists?number={number} - Vérifie si un numéro existe
     */
    @GetMapping("/team/{teamId}/number-exists")
    public ResponseEntity<Boolean> numberExistsInTeam(
            @PathVariable Long teamId,
            @RequestParam Integer number) {
        boolean exists = playerService.numberExistsInTeam(number, teamId);
        return ResponseEntity.ok(exists);
    }

    /**
     * POST /api/players - Crée un nouveau joueur
     */
    @PostMapping
    public ResponseEntity<PlayerDTO> createPlayer(@Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO createdPlayer = playerService.createPlayer(playerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/players/{id} - Met à jour un joueur existant
     */
    @PutMapping("/{id}")
    public ResponseEntity<PlayerDTO> updatePlayer(
            @PathVariable Long id,
            @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO updatedPlayer = playerService.updatePlayer(id, playerDTO);
            return ResponseEntity.ok(updatedPlayer);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/players/{id} - Supprime un joueur
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable Long id) {
        try {
            playerService.deletePlayer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/players/team/{teamId} - Supprime tous les joueurs d'une équipe
     */
    @DeleteMapping("/team/{teamId}")
    public ResponseEntity<Void> deletePlayersByTeamId(@PathVariable Long teamId) {
        playerService.deletePlayersByTeamId(teamId);
        return ResponseEntity.noContent().build();
    }
}