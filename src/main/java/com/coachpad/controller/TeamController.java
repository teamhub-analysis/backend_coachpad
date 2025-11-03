package com.coachpad.controller;

import com.coachpad.dto.TeamDTO;
import com.coachpad.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeamController {

    private final TeamService teamService;

    /**
     * GET /api/teams - Récupère toutes les équipes
     */
    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    /**
     * GET /api/teams/{id} - Récupère une équipe par ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/teams/name/{name} - Récupère une équipe par nom exact
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<TeamDTO> getTeamByName(@PathVariable String name) {
        return teamService.getTeamByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/teams/search?name={name} - Recherche des équipes par nom
     */
    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeamsByName(@RequestParam String name) {
        List<TeamDTO> teams = teamService.searchTeamsByName(name);
        return ResponseEntity.ok(teams);
    }

    /**
     * GET /api/teams/formation/{formationId} - Récupère les équipes par formation
     */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByFormationId(@PathVariable Long formationId) {
        List<TeamDTO> teams = teamService.getTeamsByFormationId(formationId);
        return ResponseEntity.ok(teams);
    }

    /**
     * GET /api/teams/coach/{coachId} - Récupère l'équipe d'un entraîneur
     */
    @GetMapping("/coach/{coachId}")
    public ResponseEntity<TeamDTO> getTeamByHeadCoachId(@PathVariable Long coachId) {
        return teamService.getTeamByHeadCoachId(coachId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * GET /api/teams/count - Compte le nombre d'équipes
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTeams() {
        long count = teamService.countTeams();
        return ResponseEntity.ok(count);
    }

    /**
     * GET /api/teams/name-exists?name={name} - Vérifie si un nom d'équipe existe
     */
    @GetMapping("/name-exists")
    public ResponseEntity<Boolean> teamNameExists(@RequestParam String name) {
        boolean exists = teamService.teamNameExists(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * POST /api/teams - Crée une nouvelle équipe
     */
    @PostMapping
    public ResponseEntity<TeamDTO> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO createdTeam = teamService.createTeam(teamDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * PUT /api/teams/{id} - Met à jour une équipe existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<TeamDTO> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
            return ResponseEntity.ok(updatedTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}/design")
    public ResponseEntity<TeamDTO> removeDesignFromTeam(@PathVariable Long id) {
        try {
            TeamDTO team = teamService.removeDesignFromTeam(id);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}