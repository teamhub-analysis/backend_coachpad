package com.coachpad.controller;

import com.coachpad.dto.*;
import com.coachpad.service.PlanningDomainService;
import com.coachpad.persistence.entity.UserEntity;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/planning")
@RequiredArgsConstructor
public class PlanningController {

    private final PlanningDomainService planningService;

    // ===== MACROCYCLES =====

    @GetMapping("/macrocycles")
    public List<MacrocycleDTO> getAllMacrocycles(@AuthenticationPrincipal UserEntity user) {
        return planningService.getAllMacrocycles(user.getId());
    }

    @GetMapping("/macrocycles/{id}")
    public ResponseEntity<MacrocycleDTO> getMacrocycle(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMacrocycleById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/macrocycles")
    public MacrocycleDTO saveMacrocycle(
            @RequestBody MacrocycleDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveMacrocycle(dto, user.getId());
    }

    @DeleteMapping("/macrocycles/{id}")
    public ResponseEntity<Void> deleteMacrocycle(@PathVariable String id) {
        planningService.deleteMacrocycle(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MESOCYCLES =====

    @GetMapping("/mesocycles/macrocycle/{macrocycleId}")
    public List<MesocycleDTO> getMesocyclesForMacrocycle(
            @PathVariable String macrocycleId,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMesocyclesForMacrocycle(macrocycleId, user.getId());
    }

    @GetMapping("/mesocycles/{id}")
    public ResponseEntity<MesocycleDTO> getMesocycle(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMesocycleById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/mesocycles")
    public MesocycleDTO saveMesocycle(
            @RequestBody MesocycleDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveMesocycle(dto, user.getId());
    }

    @DeleteMapping("/mesocycles/{id}")
    public ResponseEntity<Void> deleteMesocycle(@PathVariable String id) {
        planningService.deleteMesocycle(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MICROCYCLES =====

    @GetMapping("/microcycles/mesocycle/{mesocycleId}")
    public List<MicrocycleDTO> getMicrocyclesForMesocycle(
            @PathVariable String mesocycleId,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMicrocyclesForMesocycle(mesocycleId, user.getId());
    }

    @GetMapping("/microcycles/{id}")
    public ResponseEntity<MicrocycleDTO> getMicrocycle(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMicrocycleById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/microcycles")
    public MicrocycleDTO saveMicrocycle(
            @RequestBody MicrocycleDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveMicrocycle(dto, user.getId());
    }

    @PostMapping("/microcycles/generate/{mesocycleId}")
    public List<MicrocycleDTO> generateMicrocycles(
            @PathVariable String mesocycleId,
            @RequestParam(required = false, defaultValue = "Préparation") String weekType,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.generateMicrocycles(mesocycleId, user.getId(), weekType);
    }

    @DeleteMapping("/microcycles/{id}")
    public ResponseEntity<Void> deleteMicrocycle(@PathVariable String id) {
        planningService.deleteMicrocycle(id);
        return ResponseEntity.noContent().build();
    }

    // ===== SESSIONS =====

    @GetMapping("/sessions/microcycle/{microcycleId}")
    public List<TrainingSessionDTO> getSessionsForMicrocycle(
            @PathVariable String microcycleId,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getSessionsForMicrocycle(microcycleId, user.getId());
    }

    @GetMapping("/sessions/{id}")
    public ResponseEntity<TrainingSessionDTO> getSession(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getSessionById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/sessions")
    public TrainingSessionDTO saveSession(
            @RequestBody TrainingSessionDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveSession(dto, user.getId());
    }

    @DeleteMapping("/sessions/{id}")
    public ResponseEntity<Void> deleteSession(@PathVariable String id) {
        planningService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    // ===== DRILLS =====

    @GetMapping("/drills/session/{sessionId}")
    public List<DrillDTO> getDrillsForSession(
            @PathVariable String sessionId,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getDrillsForSession(sessionId, user.getId());
    }

    @GetMapping("/drills/{id}")
    public ResponseEntity<DrillDTO> getDrill(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getDrillById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/drills")
    public DrillDTO saveDrill(
            @RequestBody DrillDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveDrill(dto, user.getId());
    }

    @DeleteMapping("/drills/{id}")
    public ResponseEntity<Void> deleteDrill(@PathVariable String id) {
        planningService.deleteDrill(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MATCHES =====

    @GetMapping("/matches/microcycle/{microcycleId}")
    public List<MatchEventDTO> getMatchesForMicrocycle(
            @PathVariable String microcycleId,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMatchesForMicrocycle(microcycleId, user.getId());
    }

    @GetMapping("/matches/{id}")
    public ResponseEntity<MatchEventDTO> getMatch(
            @PathVariable String id,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.getMatchById(id, user.getId())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/matches")
    public MatchEventDTO saveMatch(
            @RequestBody MatchEventDTO dto,
            @AuthenticationPrincipal UserEntity user) {
        return planningService.saveMatch(dto, user.getId());
    }

    @DeleteMapping("/matches/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable String id) {
        planningService.deleteMatch(id);
        return ResponseEntity.noContent().build();
    }
}
