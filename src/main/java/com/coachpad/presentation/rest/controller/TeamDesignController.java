package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.usecase.TeamUseCase;
import com.coachpad.presentation.rest.dto.TeamDesignDTO;
import com.coachpad.presentation.rest.mapper.TeamDesignDTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team-designs")
@RequiredArgsConstructor
public class TeamDesignController {

    private final TeamUseCase teamUseCase;
    private final TeamDesignDTOMapper teamDesignDTOMapper;

    @GetMapping
    public ResponseEntity<List<TeamDesignDTO>> getAllTeamDesigns() {
        List<TeamDesignDTO> designs = teamUseCase.getAllTeamDesigns().stream()
                .map(teamDesignDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(designs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeamDesignDTO> getTeamDesignById(@PathVariable Long id) {
        return teamUseCase.getTeamDesignById(id)
                .map(teamDesignDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/team/{teamId}")
    public ResponseEntity<TeamDesignDTO> getDesignByTeamId(@PathVariable Long teamId) {
        return teamUseCase.getTeamDesign(teamId)
                .map(teamDesignDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<TeamDesignDTO> createTeamDesign(@Valid @RequestBody TeamDesignDTO dto) {
        TeamDesignDTO created = teamDesignDTOMapper.toDTO(
                teamUseCase.createTeamDesign(teamDesignDTOMapper.toModel(dto)));
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TeamDesignDTO> updateTeamDesign(
            @PathVariable Long id,
            @Valid @RequestBody TeamDesignDTO dto) {
        TeamDesignDTO updated = teamDesignDTOMapper.toDTO(
                teamUseCase.updateTeamDesign(id, teamDesignDTOMapper.toModel(dto)));
        return ResponseEntity.ok(updated);
    }

}
