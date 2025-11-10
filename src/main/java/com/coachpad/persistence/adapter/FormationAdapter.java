// src/main/java/com/coachpad/persistence/adapter/FormationAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.mapper.FormationMapper;
import com.coachpad.model.FormationModel;
import com.coachpad.persistence.entity.FormationEntity;
import com.coachpad.persistence.repository.FormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FormationAdapter {

    private final FormationRepository formationRepository;
    private final FormationMapper mapper; 

    public List<FormationModel> findAll() {
        return formationRepository.findAll()
                .stream()
                .map(mapper::toModel)
                .toList();
    }

    public Optional<FormationModel> findById(Long id) {
        return formationRepository.findById(id)
                .map(mapper::toModel);
    }

    public FormationModel save(FormationModel model) {
        FormationEntity entity = mapper.toEntity(model);
        FormationEntity saved = formationRepository.save(entity);
        return mapper.toModel(saved);
    }

    public void deleteById(Long id) {
        formationRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return formationRepository.existsById(id);
    }
}
