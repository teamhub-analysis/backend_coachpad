// src/main/java/com/coachpad/persistence/adapter/TeamDesignAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.persistence.entity.TeamDesignEntity;
import com.coachpad.persistence.repository.TeamDesignRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamDesignAdapter {

    private final TeamDesignRepository repository;
    private final TeamDesignMapper mapper;

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public List<TeamDesignDTO> findAll() {
        log.debug("Récupération de tous les designs d'équipe");
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .toList(); // Java 16+
    }

    @Transactional(readOnly = true)
    public TeamDesignDTO findById(Long id) {
        log.debug("Récupération du design avec l'ID: {}", id);
        TeamDesignEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design not found: " + id));
        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public boolean exists(Long id) {
        return repository.existsById(id);
    }

    @Transactional(readOnly = true)
    public List<TeamDesignDTO> findByStyle(String style) {
        log.debug("Recherche des designs avec le style: {}", style);
        return repository.findByStyle(style).stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<TeamDesignDTO> findByJerseyDesign(String jerseyDesign) {
        log.debug("Recherche des designs avec le maillot: {}", jerseyDesign);
        return repository.findByJerseyDesign(jerseyDesign).stream()
                .map(mapper::toDTO)
                .toList();
    }

    // ==================== WRITE OPERATIONS ====================

    @Transactional
    public TeamDesignDTO create(TeamDesignDTO dto) {
        log.debug("Création d'un nouveau design d'équipe: {}", dto);
        validateDesign(dto);

        TeamDesignEntity entity = mapper.toEntity(dto);
        TeamDesignEntity saved = repository.save(entity);

        log.info("Design créé avec succès | ID: {}", saved.getId());
        return mapper.toDTO(saved);
    }

    @Transactional
    public TeamDesignDTO update(Long id, TeamDesignDTO dto) {
        log.debug("Mise à jour complète du design | ID: {}", id);
        validateDesign(dto);

        TeamDesignEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design not found: " + id));

        mapper.updateEntityFromDTO(dto, entity);
        TeamDesignEntity updated = repository.save(entity);

        log.info("Design mis à jour avec succès | ID: {}", id);
        return mapper.toDTO(updated);
    }

    @Transactional
    public TeamDesignDTO partialUpdate(Long id, TeamDesignDTO dto) {
        log.debug("Mise à jour partielle du design | ID: {}", id);

        TeamDesignEntity entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Design not found: " + id));

        // Mise à jour conditionnelle
        if (dto.getStyle() != null) {
            entity.setStyle(dto.getStyle());
        }
        if (dto.getLogoFilePath() != null) {
            entity.setLogoFilePath(dto.getLogoFilePath());
        }
        if (dto.getLogoIconName() != null) {
            entity.setLogoIconName(dto.getLogoIconName());
        }
        if (dto.getJerseyDesign() != null) {
            entity.setJerseyDesign(dto.getJerseyDesign());
        }
        if (dto.getColors() != null) {
            if (entity.getColors() == null) {
                entity.setColors(mapper.toEntity(dto).getColors());
            } else {
                // Utilise le mapper dédié aux couleurs
                // (ou injecte TeamKitColorsMapper si besoin)
                entity.getColors().setPrimaryHex(dto.getColors().getPrimaryHex());
                entity.getColors().setSecondaryHex(dto.getColors().getSecondaryHex());
                entity.getColors().setTrimHex(dto.getColors().getTrimHex());
            }
        }

        TeamDesignEntity updated = repository.save(entity);
        log.info("Design partiellement mis à jour | ID: {}", id);
        return mapper.toDTO(updated);
    }

    // ==================== VALIDATION ====================

    private void validateDesign(TeamDesignDTO dto) {
    if (dto == null) {
        throw new ValidationException("Le DTO du design ne peut pas être null");
    }
    if (dto.getColors() == null) {
        throw new ValidationException("Les couleurs sont obligatoires");
    }
    if (dto.getJerseyDesign() == null) {
        throw new ValidationException("Le design du maillot est obligatoire");
    }

    // trim() sécurisé sur les String seulement
    boolean hasLogo = (dto.getLogoFilePath() != null && !dto.getLogoFilePath().trim().isEmpty()) ||
                      (dto.getLogoIconName() != null && !dto.getLogoIconName().trim().isEmpty());

    if (!hasLogo) {
        log.warn("Aucun logo fourni pour le design | Un logo par défaut sera utilisé");
    }
}
}