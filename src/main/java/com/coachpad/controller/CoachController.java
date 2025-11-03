package com.coachpad.controller;

import com.coachpad.model.CoachModel;
import com.coachpad.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/coaches")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CoachController {

    private final CoachService coachService;

    @GetMapping
    public List<CoachModel> getAllCoaches() {
        return coachService.getAllCoaches();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<CoachModel>> getCoachById(@PathVariable Long id) {
        return ResponseEntity.ok(coachService.getCoachById(id));
    }

    @PostMapping
    public ResponseEntity<CoachModel> createCoach(@RequestBody CoachModel coach) {
        return ResponseEntity.ok(coachService.createCoach(coach));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CoachModel> updateCoach(@PathVariable Long id, @RequestBody CoachModel coach) {
        return ResponseEntity.ok(coachService.updateCoach(id, coach));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
        return ResponseEntity.noContent().build();
    }
}
