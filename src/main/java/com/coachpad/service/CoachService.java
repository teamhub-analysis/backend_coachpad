package com.coachpad.service;

import com.coachpad.model.CoachModel;

import java.util.List;
import java.util.Optional;

public interface CoachService {

    List<CoachModel> getAllCoaches();

    Optional<CoachModel> getCoachById(Long id);

    Optional<CoachModel> getCoachByFullName(String fullName);

    CoachModel createCoach(CoachModel coach);

    CoachModel updateCoach(Long id, CoachModel coach);

    void deleteCoach(Long id);

    /**
     * Récupérer le coach principal d'une équipe
     */
    Optional<CoachModel> getCoachByTeamId(Long teamId);
}
