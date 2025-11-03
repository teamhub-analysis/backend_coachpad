package com.coachpad.persistence.adapter;

import com.coachpad.mapper.FormationMapper;
import com.coachpad.model.FormationModel;
import com.coachpad.persistence.entity.FormationEntity;
import com.coachpad.persistence.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FormationAdapter {

    private final FormationRepository formationRepository;

    /**
     * Récupère toutes les formations
     */
    public List<FormationModel> findAll() {
        return formationRepository.findAll()
                .stream()
                .map(FormationMapper::toModel)
                .collect(Collectors.toList());
    }

    /**
     * Récupère une formation par ID
     */
    public Optional<FormationModel> findById(Long id) {
        return formationRepository.findById(id)
                .map(FormationMapper::toModel);
    }

    /**
     * Sauvegarde ou met à jour une formation
     */
    public FormationModel save(FormationModel model) {
        FormationEntity entity = FormationMapper.toEntity(model);
        FormationEntity saved = formationRepository.save(entity);
        return FormationMapper.toModel(saved);
    }

    /**
     * Supprime une formation par ID
     */
    public void deleteById(Long id) {
        formationRepository.deleteById(id);
    }

    /**
     * Vérifie si une formation existe
     */
    public boolean existsById(Long id) {
        return formationRepository.existsById(id);
    }
}
