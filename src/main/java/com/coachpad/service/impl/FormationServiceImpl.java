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
    public FormationDTO createFormation(FormationDTO dto) {
        if (formationRepository.existsByName(dto.getName())) {
            throw new IllegalArgumentException("Une formation avec ce nom existe déjà : " + dto.getName());
        }
        FormationEntity entity = formationMapper.toEntity(dto);
        FormationEntity saved = formationRepository.save(entity);
        return formationMapper.toDto(saved);
    }

    @Override
    public FormationDTO updateFormation(Long id, FormationDTO dto) {
        FormationEntity existing = formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec id : " + id));

        existing.setName(dto.getName());
        existing.setOrderedPositions(dto.getOrderedPositions());

        FormationEntity updated = formationRepository.save(existing);
        return formationMapper.toDto(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public FormationDTO getFormationById(Long id) {
        FormationEntity entity = formationRepository.findByIdWithTeams(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée avec id : " + id));
        return formationMapper.toDto(entity);
    }

    @Override
    public void deleteFormation(Long id) {
        if (!formationRepository.existsById(id)) {
            throw new EntityNotFoundException("Formation non trouvée avec id : " + id);
        }
        formationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getAllFormations() {
        return formationRepository.findAllWithPositions()
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> searchByName(String name) {
        return formationRepository.searchByName(name)
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getValidFormations() {
        return formationRepository.findValidFormations()
                .stream()
                .map(formationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FormationDTO> getMostUsedFormations() {
        return formationRepository.findMostUsedFormations()
                .stream()
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
        return formationRepository.countTeamsUsingFormation(formationId);
    }
}
