package com.coachpad.infrastructure.persistance.postgresql.adapter;

import com.coachpad.domain.model.FormationModel;
import com.coachpad.infrastructure.persistance.postgresql.entity.FormationEntity;
import com.coachpad.infrastructure.persistance.postgresql.repository.FormationJpaRepository;
import com.coachpad.domain.repository.FormationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class FormationPostgresqlAdapter implements FormationRepository {

    private final FormationJpaRepository FormationJpaRepository;

    @Override
    public List<FormationModel> createFormation(List<FormationModel> models) {
        if (models == null || models.isEmpty()) {
            throw new IllegalArgumentException("La liste de formations ne peut pas Ãªtre vide.");
        }

        for (FormationModel model : models) {
            if (model.getOrderedPositions() == null || model.getOrderedPositions().isEmpty()) {
                throw new IllegalArgumentException("Les positions de la formation sont obligatoires pour toutes les formations de la liste.");
            }
            if (model.getOrderedPositions().size() != 11) {
                throw new IllegalArgumentException("Une formation doit contenir exactement 11 positions.");
            }
        }

        List<FormationEntity> entities = models.stream()
                                                .map(this::toEntity)
                                                .toList();

        List<FormationEntity> savedEntities = FormationJpaRepository.saveAll(entities);
        return savedEntities.stream()
                         .map(this::toModel)
                         .toList();
    }

    @Override
    public FormationModel updateFormation(Long id, FormationModel model) {
        FormationEntity existing = FormationJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvÃ©e avec id : " + id));

        if (model.getOrderedPositions() != null && !model.getOrderedPositions().isEmpty()) {
            existing.setOrderedPositions(model.getOrderedPositions());
        }

        FormationEntity updated = FormationJpaRepository.save(existing);
        return toModel(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FormationModel getFormationById(Long id) {
        FormationEntity entity = FormationJpaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvÃ©e avec id : " + id));
        return toModel(entity);
    }

    @Override
    public void deleteFormation(Long id) {
        if (!FormationJpaRepository.existsById(id)) {
            throw new EntityNotFoundException("Formation non trouvÃ©e avec id : " + id);
        }

        long teamsCount = FormationJpaRepository.countTeamsUsingFormation(id);
        if (teamsCount > 0) {
            throw new IllegalStateException(
                "Impossible de supprimer cette formation car elle est utilisÃ©e par " + teamsCount + " Ã©quipe(s)"
            );
        }

        FormationJpaRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationModel> getAllFormations() {
        return FormationJpaRepository.findAll()
                .stream()
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationModel> getValidFormations() {
        return FormationJpaRepository.findAll()
                .stream()
                .filter(FormationEntity::isValid)
                .map(this::toModel)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeamsUsingFormation(Long formationId) {
        if (!FormationJpaRepository.existsById(formationId)) {
            throw new EntityNotFoundException("Formation non trouvÃ©e avec id : " + formationId);
        }
        return FormationJpaRepository.countTeamsUsingFormation(formationId);
    }

    private FormationModel toModel(FormationEntity entity) {
        return FormationModel.builder()
                .id(entity.getId())
                .orderedPositions(entity.getOrderedPositions())
                .build();
    }

    private FormationEntity toEntity(FormationModel model) {
        return FormationEntity.builder()
                .id(model.getId())
                .orderedPositions(model.getOrderedPositions())
                .build();
    }
}
