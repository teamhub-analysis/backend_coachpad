package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.domain.usecase.CoachUseCase;
import com.coachpad.presentation.rest.dto.CoachDTO;
import com.coachpad.presentation.rest.mapper.CoachDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
public class CoachController {

    private final CoachUseCase coachUseCase;
    private final CoachDTOMapper coachDTOMapper;

    @GetMapping
    public List<CoachDTO> getAllCoaches() {
        return coachUseCase.getAllCoaches().stream()
                .map(coachDTOMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CoachDTO> getCoachById(@PathVariable Long id) {
        return coachUseCase.getCoachById(id)
                .map(coachDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<CoachDTO> createCoach(@RequestBody CoachDTO coach) {
        CoachModel model = coachDTOMapper.toModel(coach);
        CoachModel created = coachUseCase.createCoach(model);
        return ResponseEntity.ok(coachDTOMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoachDTO> updateCoach(@PathVariable Long id, @RequestBody CoachDTO coach) {
        CoachModel model = coachDTOMapper.toModel(coach);
        CoachModel updated = coachUseCase.updateCoach(id, model);
        return ResponseEntity.ok(coachDTOMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachUseCase.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
}
