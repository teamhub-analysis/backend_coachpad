package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.CoachEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.CoachJpaRepository;
import com.coachpad.domain.repository.CoachRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CoachPostgresqlAdapter implements CoachRepository {

    private final CoachJpaRepository coachJpaRepository;

    @Override
    public List<CoachModel> getAllCoaches() {
        return coachJpaRepository.findAll()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CoachModel> getCoachById(Long id) {
        return coachJpaRepository.findById(id).map(this::toModel);
    }

    @Override
    public Optional<CoachModel> getCoachByFullName(String fullName) {
        return coachJpaRepository.findByFullName(fullName).map(this::toModel);
    }

    @Override
    public CoachModel createCoach(CoachModel coach) {
        CoachEntity entity = toEntity(coach);
        CoachEntity saved = coachJpaRepository.save(entity);
        return toModel(saved);
    }

    @Override
    public CoachModel updateCoach(Long id, CoachModel coach) {
        CoachEntity existing = coachJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coach non trouvÃ© avec id : " + id));

        existing.setFirstName(coach.getFirstName());
        existing.setLastName(coach.getLastName());
        existing.setFullName(coach.getFullName());
        existing.setNationality(coach.getNationality());
        existing.setPhotoUrl(coach.getPhotoUrl());
        existing.setLicenseLevel(coach.getLicenseLevel());
        existing.setContractEndDate(coach.getContractEndDate());
        existing.setCoachingPhilosophy(coach.getCoachingPhilosophy());
        existing.setCoachingPhilosophyDescription(coach.getCoachingPhilosophyDescription());

        CoachEntity updated = coachJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    public void deleteCoach(Long id) {
        coachJpaRepository.deleteById(id);
    }

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
