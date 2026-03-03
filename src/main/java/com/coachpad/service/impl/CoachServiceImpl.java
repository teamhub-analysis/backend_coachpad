package com.coachpad.service.impl;

import com.coachpad.model.CoachModel;
import com.coachpad.persistence.entity.CoachEntity;
import com.coachpad.persistence.entity.TeamEntity;
import com.coachpad.persistence.repository.CoachRepository;
import com.coachpad.persistence.repository.TeamRepository;
import com.coachpad.service.CoachService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoachServiceImpl implements CoachService {

    private final CoachRepository coachRepository;
    private final TeamRepository teamRepository;

    @Override
    public List<CoachModel> getAllCoaches() {
        return coachRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CoachModel> getCoachById(Long id) {
        return coachRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<CoachModel> getCoachByFullName(String fullName) {
        return coachRepository.findByFullName(fullName).map(this::toModel);
    }

    @Override
    public CoachModel createCoach(CoachModel coach) {
        CoachEntity entity = toEntity(coach);
        CoachEntity saved = coachRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public CoachModel updateCoach(Long id, CoachModel coach) {
        CoachEntity existing = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach non trouvé avec id : " + id));

        existing.setFirstName(coach.getFirstName());
        existing.setLastName(coach.getLastName());
        existing.setFullName(coach.getFullName());
        existing.setNationality(coach.getNationality());
        existing.setPhotoUrl(coach.getPhotoUrl());
        existing.setLicenseLevel(coach.getLicenseLevel());
        existing.setContractEndDate(coach.getContractEndDate());
        existing.setCoachingPhilosophy(coach.getCoachingPhilosophy());
        existing.setCoachingPhilosophyDescription(coach.getCoachingPhilosophyDescription());

        CoachEntity updated = coachRepository.save(existing);
        return toModel(updated);
    }

    @Override
    public void deleteCoach(Long id) {
        coachRepository.deleteById(id);
    }

    @Override
    public Optional<CoachModel> getCoachByTeamId(Long teamId) {
        Optional<TeamEntity> teamOpt = teamRepository.findById(teamId);
        return teamOpt.flatMap(team -> team.getCoaches().stream()
                .filter(c -> c.getRole() == com.coachpad.persistence.Enum.CoachRole.HEAD_COACH)
                .findFirst()
                .or(() -> team.getCoaches().stream().findFirst())
                .map(this::toModel));
    }

    // --- Mapping ---
    private CoachEntity toEntity(CoachModel coach) {
        return CoachEntity.builder()
                .id(coach.getId())
                .firstName(coach.getFirstName())
                .lastName(coach.getLastName())
                .fullName(coach.getFullName())
                .nationality(coach.getNationality())
                .photoUrl(coach.getPhotoUrl())
                .licenseLevel(coach.getLicenseLevel())
                .contractEndDate(coach.getContractEndDate())
                .coachingPhilosophy(coach.getCoachingPhilosophy())
                .coachingPhilosophyDescription(coach.getCoachingPhilosophyDescription())
                .role(coach.getRole())
                .build();
    }

    private CoachModel toModel(CoachEntity entity) {
        return CoachModel.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .fullName(entity.getFullName())
                .nationality(entity.getNationality())
                .photoUrl(entity.getPhotoUrl())
                .licenseLevel(entity.getLicenseLevel())
                .contractEndDate(entity.getContractEndDate())
                .coachingPhilosophy(entity.getCoachingPhilosophy())
                .coachingPhilosophyDescription(entity.getCoachingPhilosophyDescription())
                .role(entity.getRole())
                .assigned(entity.isAssigned())
                .build();
    }
}
