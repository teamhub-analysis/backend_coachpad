package com.coachpad.controller;

import com.coachpad.dto.TeamDTO;
import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.service.TeamService;
import com.coachpad.service.TeamDesignService;
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
    private final TeamDesignService teamDesignService;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamService.getAllTeams();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return teamService.getTeamById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TeamDTO> getTeamByName(@PathVariable String name) {
        return teamService.getTeamByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeamsByName(@RequestParam String name) {
        List<TeamDTO> teams = teamService.searchTeamsByName(name);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByFormationId(@PathVariable Long formationId) {
        List<TeamDTO> teams = teamService.getTeamsByFormationId(formationId);
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<TeamDTO> getTeamByHeadCoachId(@PathVariable Long coachId) {
        return teamService.getTeamByHeadCoachId(coachId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTeams() {
        long count = teamService.countTeams();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/name-exists")
    public ResponseEntity<Boolean> teamNameExists(@RequestParam String name) {
        boolean exists = teamService.teamNameExists(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO createdTeam = teamService.createTeam(teamDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTeam(
            @PathVariable Long id,
            @Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamDTO updatedTeam = teamService.updateTeam(id, teamDTO);
            return ResponseEntity.ok(updatedTeam);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamService.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== ENDPOINTS POUR LE DESIGN DE L'ÉQUIPE ==========

    /**
     * GET /api/teams/{teamId}/design - Récupère le design d'une équipe
     */
    @GetMapping("/{teamId}/design")
    public ResponseEntity<TeamDesignDTO> getTeamDesign(@PathVariable Long teamId) {
        return teamDesignService.getDesignByTeamId(teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * POST /api/teams/{teamId}/design - Crée un design pour une équipe
     */
    @PostMapping("/{teamId}/design")
public ResponseEntity<?> createTeamDesign(
        @PathVariable Long teamId,
        @Valid @RequestBody TeamDesignDTO designDTO) {
    try {
        // Vérifier que l'équipe existe
        if (teamService.getTeamById(teamId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // ✅ AJOUT : Associer le teamId au DTO avant de créer le design
        designDTO.setTeamId(teamId);
        
        TeamDesignDTO created = teamDesignService.createDesign(designDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

    /**
     * PUT /api/teams/{teamId}/design - Met à jour le design d'une équipe
     */
    @PutMapping("/{teamId}/design")
    public ResponseEntity<?> updateTeamDesign(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDesignDTO designDTO) {
        try {
            // Récupérer le design existant de l'équipe
            TeamDesignDTO existingDesign = teamDesignService.getDesignByTeamId(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Design non trouvé pour cette équipe"));
            
            TeamDesignDTO updated = teamDesignService.updateDesign(existingDesign.getId(), designDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * DELETE /api/teams/{teamId}/design - Supprime le design d'une équipe
     */
    @DeleteMapping("/{teamId}/design")
    public ResponseEntity<?> removeDesignFromTeam(@PathVariable Long teamId) {
        try {
            TeamDTO team = teamService.removeDesignFromTeam(teamId);
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}