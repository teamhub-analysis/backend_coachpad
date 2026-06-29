package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.TeamUseCase;
import com.coachpad.presentation.rest.dto.TeamDTO;
import com.coachpad.presentation.rest.dto.TeamDesignDTO;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.presentation.rest.mapper.TeamDTOMapper;
import com.coachpad.presentation.rest.mapper.TeamDesignDTOMapper;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamModel;
import com.coachpad.domain.model.TeamDesignModel;
import com.coachpad.infrastructure.service.storage.FileStorageService;
import com.coachpad.infrastructure.service.dataimport.ExcelImportService;
import com.coachpad.infrastructure.service.dataimport.CsvImportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/teams")
@RequiredArgsConstructor
public class TeamController {

    private final TeamUseCase teamUseCase;
    private final FileStorageService fileStorageService;
    private final ExcelImportService excelImportService;
    private final CsvImportService csvImportService;
    private final TeamDTOMapper teamDTOMapper;
    private final TeamDesignDTOMapper teamDesignDTOMapper;
    private final PlayerDTOMapper playerDTOMapper;

    @GetMapping
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        List<TeamDTO> teams = teamUseCase.getAllTeams().stream()
                .map(teamDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDTO> getTeamById(@PathVariable Long id) {
        return teamUseCase.getTeamById(id)
                .map(teamDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/name/{name}")
    public ResponseEntity<TeamDTO> getTeamByName(@PathVariable String name) {
        return teamUseCase.getTeamByName(name)
                .map(teamDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<TeamDTO>> searchTeamsByName(@RequestParam String name) {
        List<TeamDTO> teams = teamUseCase.searchTeamsByName(name).stream()
                .map(teamDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<TeamDTO>> getTeamsByFormationId(@PathVariable Long formationId) {
        List<TeamDTO> teams = teamUseCase.getTeamsByFormationId(formationId).stream()
                .map(teamDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(teams);
    }

    @GetMapping("/coach/{coachId}")
    public ResponseEntity<TeamDTO> getTeamByHeadCoachId(@PathVariable Long coachId) {
        return teamUseCase.getTeamByHeadCoachId(coachId)
                .map(teamDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countTeams() {
        long count = teamUseCase.countTeams();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/name-exists")
    public ResponseEntity<Boolean> teamNameExists(@RequestParam String name) {
        boolean exists = teamUseCase.teamNameExists(name);
        return ResponseEntity.ok(exists);
    }

    @PostMapping
    public ResponseEntity<?> createTeam(@Valid @RequestBody TeamDTO teamDTO) {
        try {
            TeamModel model = teamDTOMapper.toModel(teamDTO);
            TeamDTO createdTeam = teamDTOMapper.toDTO(teamUseCase.createTeam(model));
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
            TeamModel model = teamDTOMapper.toModel(teamDTO);
            TeamDTO updatedTeam = teamDTOMapper.toDTO(teamUseCase.updateTeam(id, model));
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

    @DeleteMapping("/cleanup-excel")
    public ResponseEntity<Void> cleanupExcelTeams() {
        teamUseCase.cleanupExcelTeams();
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTeam(@PathVariable Long id) {
        try {
            teamUseCase.deleteTeam(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{teamId}/design")
    public ResponseEntity<TeamDesignDTO> getTeamDesign(@PathVariable Long teamId) {
        return teamUseCase.getTeamDesign(teamId)
                .map(teamDesignDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{teamId}/design")
    public ResponseEntity<?> createTeamDesign(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDesignDTO designDTO) {
        try {
            if (teamUseCase.getTeamById(teamId).isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            TeamDesignModel model = teamDesignDTOMapper.toModel(designDTO);
            TeamDesignDTO created = teamDesignDTOMapper.toDTO(teamUseCase.createTeamDesign(model));
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{teamId}/design")
    public ResponseEntity<?> updateTeamDesign(
            @PathVariable Long teamId,
            @Valid @RequestBody TeamDesignDTO designDTO) {
        try {
            TeamDesignModel existingDesign = teamUseCase.getTeamDesign(teamId)
                    .orElseThrow(() -> new IllegalArgumentException("Design non trouvé pour cette équipe"));

            TeamDesignModel model = teamDesignDTOMapper.toModel(designDTO);
            TeamDesignDTO updated = teamDesignDTOMapper.toDTO(teamUseCase.updateTeamDesign(existingDesign.getId(), model));
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{teamId}/design")
    public ResponseEntity<?> removeDesignFromTeam(@PathVariable Long teamId) {
        try {
            TeamDTO team = teamDTOMapper.toDTO(teamUseCase.removeDesignFromTeam(teamId));
            return ResponseEntity.ok(team);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/logo")
    public ResponseEntity<?> updateTeamLogo(
            @PathVariable("id") Long id,
            @RequestParam("file") MultipartFile file) {
        try {
            String photoUrl = fileStorageService.storeFile(file, FileStorageService.DIR_IMAGE);
            teamUseCase.updateTeamLogo(id, photoUrl);
            TeamDTO team = teamUseCase.getTeamById(id)
                    .map(teamDTOMapper::toDTO)
                    .orElseThrow(() -> new RuntimeException("Équipe non trouvée après upload du logo"));
            return ResponseEntity.ok(team);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erreur lors de l'upload du logo : " + e.getMessage()));
        }
    }

    @GetMapping("/{teamId}/players")
    public ResponseEntity<List<PlayerDTO>> getPlayersByTeamId(@PathVariable Long teamId) {
        List<PlayerDTO> players = teamUseCase.getPlayersByTeamId(teamId).stream()
                .map(playerDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(players);
    }

    @PostMapping("/{teamId}/players")
    public ResponseEntity<PlayerDTO> addPlayerToTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody PlayerDTO playerDTO) {
        try {
            PlayerModel model = playerDTOMapper.toModel(playerDTO);
            PlayerDTO created = playerDTOMapper.toDTO(teamUseCase.addPlayerToTeam(teamId, model));
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{teamId}/players/bulk")
    public ResponseEntity<List<PlayerDTO>> addPlayersToTeam(
            @PathVariable Long teamId,
            @Valid @RequestBody List<PlayerDTO> playerDTOs) {
        try {
            List<PlayerModel> models = playerDTOs.stream()
                    .map(playerDTOMapper::toModel)
                    .toList();
            List<PlayerDTO> created = teamUseCase.addPlayersToTeam(teamId, models).stream()
                    .map(playerDTOMapper::toDTO)
                    .toList();
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{teamId}/players")
    public ResponseEntity<Void> deletePlayersByTeamId(@PathVariable Long teamId) {
        try {
            teamUseCase.deletePlayersByTeamId(teamId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/import")
    public ResponseEntity<?> importTeam(@RequestParam("file") MultipartFile file) {

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
            fileStorageService.storeFile(file, FileStorageService.DIR_FILE);

            TeamDTO importedTeam;
            if (fileName.endsWith(".csv")) {
                importedTeam = csvImportService.importFullTeam(file);
            } else {
                importedTeam = excelImportService.importFullTeam(file);
            }

            importedTeam.setCreatedAt(java.time.LocalDateTime.now());
            importedTeam.setUpdatedAt(java.time.LocalDateTime.now());

            importedTeam.setId(null);

            if (importedTeam.getPlayers() != null) {
                long tempId = -1;
                for (var player : importedTeam.getPlayers()) {
                    player.setId(tempId--);
                }
            }

            if (importedTeam.getCoaches() != null) {
                long tempCoachId = -100;
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

    @PostMapping("/import-direct")
    public ResponseEntity<?> importTeamDirect(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
        }

        try {
            TeamDTO savedTeam = excelImportService.importFullTeam(file);
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
