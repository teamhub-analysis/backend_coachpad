package com.coachpad.presentation.rest.controller;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.domain.usecase.StaffUseCase;
import com.coachpad.infrastructure.service.storage.FileStorageService;
import com.coachpad.presentation.rest.dto.CoachDTO;
import com.coachpad.presentation.rest.mapper.CoachDTOMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class StaffController {

    private final StaffUseCase staffUseCase;
    private final CoachDTOMapper coachDTOMapper;
    private final FileStorageService fileStorageService;

    @GetMapping("/teams/{teamId}/staff")
    public ResponseEntity<List<CoachDTO>> getStaffByTeamId(@PathVariable Long teamId) {
        List<CoachDTO> staff = staffUseCase.getStaffByTeamId(teamId).stream()
                .map(coachDTOMapper::toDTO)
                .toList();
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/staff/{id}")
    public ResponseEntity<CoachDTO> getStaffById(@PathVariable Long id) {
        return staffUseCase.getStaffById(id)
                .map(coachDTOMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/teams/{teamId}/staff")
    public ResponseEntity<CoachDTO> createStaff(
            @PathVariable Long teamId,
            @Valid @RequestBody CoachDTO coachDTO) {
        try {
            CoachModel model = coachDTOMapper.toModel(coachDTO);
            CoachModel created = staffUseCase.createStaff(teamId, model);
            return ResponseEntity.status(HttpStatus.CREATED).body(coachDTOMapper.toDTO(created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/staff/{id}")
    public ResponseEntity<CoachDTO> updateStaff(
            @PathVariable Long id,
            @Valid @RequestBody CoachDTO coachDTO) {
        try {
            CoachModel model = coachDTOMapper.toModel(coachDTO);
            CoachModel updated = staffUseCase.updateStaff(id, model);
            return ResponseEntity.ok(coachDTOMapper.toDTO(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/staff/{id}")
    public ResponseEntity<Void> deleteStaff(@PathVariable Long id) {
        try {
            staffUseCase.deleteStaff(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/staff/{id}/photo")
    public ResponseEntity<CoachDTO> updateStaffPhoto(
            @PathVariable Long id,
            @RequestParam MultipartFile file) {
        try {
            String photoUrl = fileStorageService.storeFile(file, FileStorageService.DIR_IMAGE);
            CoachModel updated = staffUseCase.updateStaffPhoto(id, photoUrl);
            return ResponseEntity.ok(coachDTOMapper.toDTO(updated));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
