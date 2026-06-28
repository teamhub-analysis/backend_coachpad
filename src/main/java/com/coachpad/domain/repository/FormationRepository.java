package com.coachpad.domain.repository;

import com.coachpad.domain.model.FormationModel;
import java.util.List;

public interface FormationRepository {
    List<FormationModel> createFormation(List<FormationModel> models);
    FormationModel updateFormation(Long id, FormationModel model);
    FormationModel getFormationById(Long id);
    void deleteFormation(Long id);
    List<FormationModel> getAllFormations();
    List<FormationModel> getValidFormations();
    long countTeamsUsingFormation(Long formationId);
}
