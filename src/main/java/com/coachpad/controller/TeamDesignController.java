package com.coachpad.controller;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.service.TeamDesignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-designs")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TeamDesignController {

    private final TeamDesignService teamDesignService;

    /**
     * GET /api/team-designs - Récupère tous les designs
     */
    @GetMapping
    public ResponseEntity<List<TeamDesignDTO>> getAllDesigns() {
        List<TeamDesignDTO> designs = teamDesignService.getAllDesigns();
        return ResponseEntity.ok(designs);
    }
    @GetMapping("/{id}")
    public ResponseEntity<TeamDesignDTO> getDesignById(@PathVariable Long id) {
        return teamDesignService.getDesignById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/team/{teamId}")
    public ResponseEntity<TeamDesignDTO> getDesignByTeamId(@PathVariable Long teamId) {
        return teamDesignService.getDesignByTeamId(teamId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDesign(
            @PathVariable Long id,
            @Valid @RequestBody TeamDesignDTO designDTO) {
        try {
            TeamDesignDTO updated = teamDesignService.updateDesign(id, designDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("non trouvé")) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}