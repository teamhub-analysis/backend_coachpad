package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.domain.model.enums.CoachRole;
import com.coachpad.domain.repository.StaffRepository;
import com.coachpad.infrastructure.persistance.postgresql.entity.CoachEntity;
import com.coachpad.infrastructure.persistance.postgresql.entity.TeamEntity;
import com.coachpad.infrastructure.persistance.postgresql.mapper.CoachEntityMapper;
import com.coachpad.infrastructure.persistance.postgresql.repository.CoachJpaRepository;
import com.coachpad.infrastructure.persistance.postgresql.repository.TeamJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StaffPostgresqlAdapter implements StaffRepository {

    private final CoachJpaRepository coachJpaRepository;
    private final TeamJpaRepository teamJpaRepository;
    private final CoachEntityMapper coachEntityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CoachModel> getStaffByTeamId(Long teamId) {
        return teamJpaRepository.findById(teamId)
                .map(team -> {
                    List<CoachModel> all = new ArrayList<>();
                    if (team.getCoaches() != null) {
                        all.addAll(team.getCoaches().stream()
                                .map(coachEntityMapper::toModel)
                                .toList());
                    }
                    if (team.getMedicalStaff() != null) {
                        all.addAll(team.getMedicalStaff().stream()
                                .map(coachEntityMapper::toModel)
                                .toList());
                    }
                    return all;
                })
                .orElse(List.of());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoachModel> getStaffById(Long id) {
        return coachJpaRepository.findById(id)
                .map(coachEntityMapper::toModel);
    }

    @Override
    @Transactional
    public CoachModel createStaff(Long teamId, CoachModel coach) {
        TeamEntity team = teamJpaRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team not found: " + teamId));

        CoachEntity entity = coachEntityMapper.toEntity(coach);
        entity.setTeam(team);
        entity.setId(null);

        if (isMedicalRole(coach.getRole())) {
            team.getMedicalStaff().add(entity);
        } else {
            team.getCoaches().add(entity);
        }

        teamJpaRepository.save(team);
        return coachEntityMapper.toModel(entity);
    }

    @Override
    @Transactional
    public CoachModel updateStaff(Long id, CoachModel coach) {
        CoachEntity existing = coachJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + id));

        existing.setFirstName(coach.getFirstName());
        existing.setLastName(coach.getLastName());
        existing.setFullName(coach.getFullName());
        existing.setNationality(coach.getNationality());
        existing.setLicenseLevel(coach.getLicenseLevel());
        existing.setContractEndDate(coach.getContractEndDate());
        existing.setCoachingPhilosophy(coach.getCoachingPhilosophy());
        existing.setCoachingPhilosophyDescription(coach.getCoachingPhilosophyDescription());
        existing.setRole(coach.getRole());

        CoachEntity saved = coachJpaRepository.save(existing);
        return coachEntityMapper.toModel(saved);
    }

    @Override
    @Transactional
    public void deleteStaff(Long id) {
        CoachEntity existing = coachJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + id));

        TeamEntity team = existing.getTeam();
        if (team != null) {
            team.getCoaches().remove(existing);
            team.getMedicalStaff().remove(existing);
        }

        coachJpaRepository.delete(existing);
    }

    @Override
    @Transactional
    public CoachModel updateStaffPhoto(Long id, String photoUrl) {
        CoachEntity existing = coachJpaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found: " + id));

        existing.setPhotoUrl(photoUrl);
        CoachEntity saved = coachJpaRepository.save(existing);
        return coachEntityMapper.toModel(saved);
    }

    private boolean isMedicalRole(CoachRole role) {
        return role == CoachRole.DOCTOR
                || role == CoachRole.PHYSIOTHERAPIST
                || role == CoachRole.MASSEUR
                || role == CoachRole.NUTRITIONIST;
    }
}
