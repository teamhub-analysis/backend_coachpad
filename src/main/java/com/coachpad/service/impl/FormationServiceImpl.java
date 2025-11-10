package com.coachpad.service.impl;

import com.coachpad.dto.FormationDTO;
import com.coachpad.mapper.FormationMapper;
import com.coachpad.persistence.entity.FormationEntity;
import com.coachpad.persistence.repository.FormationRepository;
import com.coachpad.service.FormationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FormationServiceImpl implements FormationService {

    private final FormationRepository formationRepository;
    private final FormationMapper formationMapper;

    @Override
    public List<FormationDTO>  createFormation(List<FormationDTO> dtos) {
        // Validation : vérifier que les positions ne sont pas vides
        if (dtos == null || dtos.isEmpty()) {
            throw new IllegalArgumentException("La liste de formations ne peut pas être vide.");
        }

        // 2. Validation de chaque formation dans la liste
        for (FormationDTO dto : dtos) {
            if (dto.getOrderedPositions() == null || dto.getOrderedPositions().isEmpty()) {
                throw new IllegalArgumentException("Les positions de la formation sont obligatoires pour toutes les formations de la liste.");
            }
            if (dto.getOrderedPositions().size() != 11) {
                throw new IllegalArgumentException("Une formation doit contenir exactement 11 positions.");
            }
        }

        // 3. Conversion de la liste de DTOs en liste d'entités
        List<FormationEntity> entities = dtos.stream()
                                              .map(formationMapper::toEntity)
                                              .toList();

        // 4. Sauvegarde de TOUTES les entités en une seule fois (très efficace)
        List<FormationEntity> savedEntities = formationRepository.saveAll(entities);
          return savedEntities.stream()
                           .map(formationMapper::toDto)
                           .toList();
    }

    @Override
    public FormationDTO updateFormation(Long id, FormationDTO dto) {
        FormationEntity existing = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec id : " + id));

        // Mise à jour des positions
        if (dto.getOrderedPositions() != null && !dto.getOrderedPositions().isEmpty()) {
            existing.setOrderedPositions(dto.getOrderedPositions());
        }

        FormationEntity updated = formationRepository.save(existing);
        return formationMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FormationDTO getFormationById(Long id) {
        FormationEntity entity = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec id : " + id));
        return formationMapper.toDto(entity);
    }

    @Override
    public void deleteFormation(Long id) {
        if (!formationRepository.existsById(id)) {
            throw new EntityNotFoundException("Formation non trouvée avec id : " + id);
        }
        
        // Vérifier si la formation est utilisée par des équipes
        long teamsCount = formationRepository.countTeamsUsingFormation(id);
        if (teamsCount > 0) {
            throw new IllegalStateException(
                "Impossible de supprimer cette formation car elle est utilisée par " + teamsCount + " équipe(s)"
            );
        }
        
        formationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getAllFormations() {
        return formationRepository.findAll()
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getValidFormations() {
        return formationRepository.findAll()
                .stream()
                .filter(FormationEntity::isValid)
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getMostUsedFormations() {
        return formationRepository.findAll()
                .stream()
                .sorted((f1, f2) -> Integer.compare(f2.getTeams().size(), f1.getTeams().size()))
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    public void deleteUnusedFormations() {
        List<FormationEntity> unusedFormations = formationRepository.findUnusedFormations();
        if (!unusedFormations.isEmpty()) {
            formationRepository.deleteAll(unusedFormations);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public long countTeamsUsingFormation(Long formationId) {
        if (!formationRepository.existsById(formationId)) {
            throw new EntityNotFoundException("Formation non trouvée avec id : " + formationId);
        }
        return formationRepository.countTeamsUsingFormation(formationId);
    }
}