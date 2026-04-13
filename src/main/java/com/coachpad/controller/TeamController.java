package com.coachpad.controller;

import com.coachpad.dto.TeamDTO;
import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.service.TeamService;
import com.coachpad.service.FileStorageService;
import com.coachpad.service.TeamDesignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import com.coachpad.dto.PlayerDTO;
import com.coachpad.service.PlayerService;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;
    private final TeamDesignService teamDesignService;
    private final FileStorageService fileStorageService;
    private final PlayerService playerService;
    private final com.coachpad.service.ExcelImportService excelImportService;
    private final com.coachpad.service.CsvImportService csvImportService;

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
        } catch (jakarta.persistence.EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de la mise à jour : " + e.getMessage()));
        }
    }

    /**
     * DELETE /api/teams/cleanup-excel - Supprime toutes les équipes importées
     * d'Excel (non-core)
     */
    @DeleteMapping("/cleanup-excel")
    public ResponseEntity<Void> cleanupExcelTeams() {
        teamService.cleanupExcelTeams();
        return ResponseEntity.noContent().build();
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

    /**
     * POST /api/teams/{id}/logo - Upload du logo de l'équipe
     */
    @PostMapping("/{id}/logo")
    public ResponseEntity<?> updateTeamLogo(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            TeamDesignDTO updated = teamDesignService.updateTeamLogo(id, file);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload du logo : " + e.getMessage()));
        }
    }

    // ========== GESTION DES JOUEURS DE L'ÉQUIPE ==========

    /**
     * GET /api/teams/{teamId}/players - Récupère tous les joueurs d'une équipe
     */
    @GetMapping("/{teamId}/players")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = playerService.getPlayersByTeamId(teamId);
        return ResponseEntity.ok(players);
    }

    /**
     * POST /api/teams/{teamId}/players - Ajoute un joueur à une équipe
     */
    @PostMapping("/{teamId}/players")
    public ResponseEntity<PlayerDTO> addPlayerToTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerDTO created = playerService.createPlayerForTeam(teamId, playerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * POST /api/teams/{teamId}/players/bulk - Ajoute plusieurs joueurs à une équipe
     */
    @PostMapping("/{teamId}/players/bulk")
    public ResponseEntity<List<PlayerDTO>> addPlayersToTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody List<PlayerDTO> playerDTOs) {
        try {
            List<PlayerDTO> created = playerService.createPlayersForTeam(teamId, playerDTOs);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * DELETE /api/teams/{teamId}/players - Supprime tous les joueurs d'une équipe
     */
    @DeleteMapping("/{teamId}/players")
    public ResponseEntity<Void> deletePlayersByTeamId(@PathVariable Long teamId) {
        try {
            playerService.deletePlayersByTeamId(teamId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // Remarque : excelImportService est maintenant déplacé avec les autres services injectés via @RequiredArgsConstructor

    /**
     * POST /api/teams/import - Importe une équipe depuis un fichier Excel pour
     * prévisualisation
     */
    @PostMapping("/import")
    public ResponseEntity<?> importTeam(@RequestParam("file") MultipartFile file) {

        // ✅ 1. Validation fichier
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Fichier vide"));
        }

        String fileName = file.getOriginalFilename();
        if (fileName == null || (!fileName.endsWith(".xlsx") && !fileName.endsWith(".csv"))) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Format invalide (seul .xlsx et .csv autorisés)"));
        }

        try {
            // ✅ 2. Sauvegarde du fichier via FileStorageService (optionnel mais demandé)
            fileStorageService.storeFile(file, "file");

            // ✅ 3. Appel bon service selon l'extension
            TeamDTO importedTeam;
            if (fileName.endsWith(".csv")) {
                importedTeam = csvImportService.importFullTeam(file);
            } else {
                importedTeam = excelImportService.importFullTeam(file);
            }

            // ✅ 3. Initialisation safe (sans casser la DB)
            importedTeam.setCreatedAt(java.time.LocalDateTime.now());
            importedTeam.setUpdatedAt(java.time.LocalDateTime.now());

            // ⚠️ NE PAS mettre d'ID fixe
            importedTeam.setId(null);

            // ✅ 4. IDs temporaires FRONT uniquement
            if (importedTeam.getPlayers() != null) {
                long tempId = -1;
                for (var player : importedTeam.getPlayers()) {
                    player.setId(tempId--); // IDs négatifs = temporaire
                }
            }

            if (importedTeam.getCoaches() != null) {
                long tempCoachId = -100; // Offset pour différencier des joueurs si besoin
                for (var coach : importedTeam.getCoaches()) {
                    coach.setId(tempCoachId--);
                }
            }

            if (importedTeam.getMedicalStaff() != null) {
                long tempMedId = -200;
                for (var staff : importedTeam.getMedicalStaff()) {
                    staff.setId(tempMedId--);
                }
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "team", importedTeam));

        } catch (RuntimeException e) {

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", e.getMessage()));

        } catch (Exception e) {

            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "error", "Erreur interne serveur"));
        }
    }

    /**
     * POST /api/teams/import-direct - Importe et SAUVEGARDE directement une équipe.
     * Le nom de l'équipe sera automatiquement préfixé si elle existe déjà.
     */
    @PostMapping("/import-direct")
    public ResponseEntity<?> importTeamDirect(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
        }

        try {
            TeamDTO savedTeam = teamService.importTeamDirect(file);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Équipe importée avec succès sous le nom : " + savedTeam.getName(),
                    "team", savedTeam));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Erreur lors de l'importation directe : " + e.getMessage()));
        }
    }
}