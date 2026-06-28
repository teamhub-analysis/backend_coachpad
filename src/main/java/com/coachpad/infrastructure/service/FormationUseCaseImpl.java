package com.coachpad.infrastructure.service;

import com.coachpad.domain.model.FormationModel;
import com.coachpad.domain.repository.FormationRepository;
import com.coachpad.domain.usecase.FormationUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationUseCaseImpl implements FormationUseCase {

    private final FormationRepository formationRepository;

    @Override
    @Transactional(readOnly = true)
    public List<FormationModel> getAllFormations() {
        return formationRepository.getAllFormations();
    }

    @Override
    @Transactional(readOnly = true)
    public FormationModel getFormationById(Long id) {
        return formationRepository.getFormationById(id);
    }

    @Override
    public List<FormationModel> createFormation(List<FormationModel> models) {
        return formationRepository.createFormation(models);
    }

    @Override
    public FormationModel updateFormation(Long id, FormationModel model) {
        return formationRepository.updateFormation(id, model);
    }

    @Override
    public void deleteFormation(Long id) {
        formationRepository.deleteFormation(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationModel> getValidFormations() {
        return formationRepository.getValidFormations();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeamsUsingFormation(Long formationId) {
        return formationRepository.countTeamsUsingFormation(formationId);
    }
}
