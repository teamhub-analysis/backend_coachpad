package com.coachpad.controller;

import com.coachpad.dto.FormationDTO;
import com.coachpad.service.FormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formations")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;

    @GetMapping
    public List<FormationDTO> getAllFormations() {
        return formationService.getAllFormations();
    }

    @GetMapping("/{id}")
    public FormationDTO getFormationById(@PathVariable Long id) {
        return formationService.getFormationById(id);
    }

    @PostMapping
    public FormationDTO createFormation(@RequestBody FormationDTO dto) {
        return formationService.createFormation(dto);
    }

    @PutMapping("/{id}")
    public FormationDTO updateFormation(@PathVariable Long id, @RequestBody FormationDTO dto) {
        return formationService.updateFormation(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteFormation(@PathVariable Long id) {
        formationService.deleteFormation(id);
    }
}
