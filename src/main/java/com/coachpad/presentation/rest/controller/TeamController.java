package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.TeamUseCase;
import com.coachpad.presentation.rest.dto.TeamDTO;
import com.coachpad.presentation.rest.dto.PlayerDTO;
import com.coachpad.presentation.rest.mapper.TeamDTOMapper;
import com.coachpad.presentation.rest.mapper.PlayerDTOMapper;
import com.coachpad.domain.model.PlayerModel;
import com.coachpad.domain.model.TeamModel;
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

    @PostMapping("/import-direct/{teamId}")
    public ResponseEntity<?> importTeamDirect(
            @PathVariable Long teamId,
            @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Fichier vide"));
        }

        try {
            String fileName = file.getOriginalFilename();
            TeamDTO parsed = fileName != null && fileName.endsWith(".csv")
                    ? csvImportService.importFullTeam(file)
                    : excelImportService.importFullTeam(file);

            TeamModel importModel = teamDTOMapper.toModel(parsed);
            importModel.setId(teamId);
            TeamModel saved = teamUseCase.replaceTeamData(teamId, importModel);
            TeamDTO savedDTO = teamDTOMapper.toDTO(saved);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Équipe importée avec succès sous le nom : " + savedDTO.getName(),
                    "team", savedDTO));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "error", "Erreur lors de l'importation directe : " + e.getMessage()));
        }
    }
}
