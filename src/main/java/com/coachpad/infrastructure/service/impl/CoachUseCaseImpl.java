package com.coachpad.infrastructure.service.impl;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.domain.repository.CoachRepository;
import com.coachpad.domain.usecase.CoachUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CoachUseCaseImpl implements CoachUseCase {

    private final CoachRepository coachRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CoachModel> getAllCoaches() {
        return coachRepository.getAllCoaches();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoachModel> getCoachById(Long id) {
        return coachRepository.getCoachById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoachModel> getCoachByFullName(String fullName) {
        return coachRepository.getCoachByFullName(fullName);
    }

    @Override
    public CoachModel createCoach(CoachModel coach) {
        return coachRepository.createCoach(coach);
    }

    @Override
    public CoachModel updateCoach(Long id, CoachModel coach) {
        return coachRepository.updateCoach(id, coach);
    }

    @Override
    public void deleteCoach(Long id) {
        coachRepository.deleteCoach(id);
    }
}
