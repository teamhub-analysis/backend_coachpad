package com.coachpad.domain.usecase;

import com.coachpad.domain.model.CoachModel;

import java.util.List;
import java.util.Optional;

public interface CoachUseCase {

    List<CoachModel> getAllCoaches();

    Optional<CoachModel> getCoachById(Long id);

    Optional<CoachModel> getCoachByFullName(String fullName);

    CoachModel createCoach(CoachModel coach);

    CoachModel updateCoach(Long id, CoachModel coach);

    void deleteCoach(Long id);
}
