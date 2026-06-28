package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.model.FormationModel;
import com.coachpad.domain.usecase.FormationUseCase;
import com.coachpad.presentation.rest.dto.FormationDTO;
import com.coachpad.presentation.rest.mapper.FormationDTOMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationUseCase formationUseCase;
    private final FormationDTOMapper formationDTOMapper;

    @GetMapping
    public List<FormationDTO> getAllFormations() {
        return formationUseCase.getAllFormations().stream()
                .map(formationDTOMapper::toDTO)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<FormationDTO> getFormationById(@PathVariable Long id) {
        FormationModel model = formationUseCase.getFormationById(id);
        return ResponseEntity.ok(formationDTOMapper.toDTO(model));
    }

    @PostMapping
    public ResponseEntity<List<FormationDTO>> createFormation(@RequestBody List<FormationDTO> dtos) {
        List<FormationModel> models = dtos.stream()
                .map(formationDTOMapper::toModel)
                .toList();
        List<FormationModel> created = formationUseCase.createFormation(models);
        List<FormationDTO> result = created.stream()
                .map(formationDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FormationDTO> updateFormation(@PathVariable Long id, @RequestBody FormationDTO dto) {
        FormationModel model = formationDTOMapper.toModel(dto);
        FormationModel updated = formationUseCase.updateFormation(id, model);
        return ResponseEntity.ok(formationDTOMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFormation(@PathVariable Long id) {
        formationUseCase.deleteFormation(id);
        return ResponseEntity.noContent().build();
    }
}
