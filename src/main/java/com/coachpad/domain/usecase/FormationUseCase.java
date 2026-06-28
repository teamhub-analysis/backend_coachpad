package com.coachpad.domain.usecase;

import com.coachpad.domain.model.FormationModel;

import java.util.List;

public interface FormationUseCase {

    List<FormationModel> getAllFormations();

    FormationModel getFormationById(Long id);

    List<FormationModel> createFormation(List<FormationModel> models);

    FormationModel updateFormation(Long id, FormationModel model);

    void deleteFormation(Long id);

    List<FormationModel> getValidFormations();

    long countTeamsUsingFormation(Long formationId);
}
