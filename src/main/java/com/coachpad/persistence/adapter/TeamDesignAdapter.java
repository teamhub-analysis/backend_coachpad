package com.coachpad.persistence.adapter;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.model.enums.JerseyDesign;
import com.coachpad.model.enums.WidgetAppearance;
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
        return repository.findAllWithColors().stream()
                .map(mapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamDesignDTO findById(Long id) {
        log.debug("Récupération du design avec l'ID: {}", id);
        TeamDesignEntity entity = repository.findByIdWithColors(id)
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
        try {
            // Convertir String en Enum
            WidgetAppearance designStyle = WidgetAppearance.valueOf(style.toUpperCase());
            return repository.findByStyle(designStyle).stream()
                    .map(mapper::toDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            log.error("Style invalide: {}", style);
            throw new ValidationException("Style invalide : " + style + 
                ". Valeurs acceptées : " + getValidStyles());
        }
    }

    @Transactional(readOnly = true)
    public List<TeamDesignDTO> findByJerseyDesign(String jerseyDesign) {
        log.debug("Recherche des designs avec le maillot: {}", jerseyDesign);
        try {
            // Convertir String en Enum
            JerseyDesign jersey = JerseyDesign.valueOf(jerseyDesign.toUpperCase());
            return repository.findByJerseyDesign(jersey).stream()
                    .map(mapper::toDTO)
                    .toList();
        } catch (IllegalArgumentException e) {
            log.error("Jersey design invalide: {}", jerseyDesign);
            throw new ValidationException("Jersey design invalide : " + jerseyDesign + 
                ". Valeurs acceptées : " + getValidJerseyDesigns());
        }
    }

    // ==================== WRITE OPERATIONS ====================

    @Transactional
    public TeamDesignDTO create(TeamDesignDTO dto) {
        log.debug("Création d'un nouveau design d'équipe: {}", dto);
        validateDesign(dto);

        TeamDesignEntity entity = mapper.toEntity(dto);
        
        // Lier les couleurs correctement
        if (entity.getColors() != null) {
            entity.getColors().setDesign(entity);
        }
        
        TeamDesignEntity saved = repository.save(entity);

        log.info("Design créé avec succès | ID: {}", saved.getId());
        return mapper.toDTO(saved);
    }

    @Transactional
    public TeamDesignDTO update(Long id, TeamDesignDTO dto) {
        log.debug("Mise à jour complète du design | ID: {}", id);
        validateDesign(dto);

        TeamDesignEntity entity = repository.findByIdWithColors(id)
                .orElseThrow(() -> new EntityNotFoundException("Design not found: " + id));

        mapper.updateEntityFromDTO(dto, entity);
        
        // Gérer les couleurs
        if (dto.getColors() != null && entity.getColors() != null) {
            entity.getColors().setDesign(entity);
        }
        
        TeamDesignEntity updated = repository.save(entity);

        log.info("Design mis à jour avec succès | ID: {}", id);
        return mapper.toDTO(updated);
    }

    @Transactional
    public TeamDesignDTO partialUpdate(Long id, TeamDesignDTO dto) {
        log.debug("Mise à jour partielle du design | ID: {}", id);

        TeamDesignEntity entity = repository.findByIdWithColors(id)
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
     
        
        // Mise à jour des couleurs
        if (dto.getColors() != null) {
            if (entity.getColors() == null) {
                TeamDesignEntity tempEntity = mapper.toEntity(dto);
                entity.setColors(tempEntity.getColors());
                if (entity.getColors() != null) {
                    entity.getColors().setDesign(entity);
                }
            } else {
                // Mise à jour des couleurs existantes
                entity.getColors().setPrimaryHex(dto.getColors().getPrimaryHex());
                entity.getColors().setSecondaryHex(dto.getColors().getSecondaryHex());
                entity.getColors().setTrimHex(dto.getColors().getTrimHex());
               
            }
        }

        TeamDesignEntity updated = repository.save(entity);
        log.info("Design partiellement mis à jour | ID: {}", id);
        return mapper.toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.debug("Suppression du design | ID: {}", id);
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Design not found: " + id);
        }
        repository.deleteById(id);
        log.info("Design supprimé avec succès | ID: {}", id);
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

        // Validation des couleurs pour les designs qui nécessitent plusieurs couleurs
        if (dto.getJerseyDesign() != null && 
            dto.getJerseyDesign().requiresMultipleColors()) {
            if (dto.getColors().getSecondaryHex() == null || 
                dto.getColors().getSecondaryHex().trim().isEmpty()) {
                throw new ValidationException(
                    "Le design " + dto.getJerseyDesign().getDisplayName() + 
                    " nécessite au moins deux couleurs"
                );
            }
        }

        // Validation du logo
        boolean hasLogo = (dto.getLogoFilePath() != null && !dto.getLogoFilePath().trim().isEmpty()) ||
                          (dto.getLogoIconName() != null && !dto.getLogoIconName().trim().isEmpty());

        if (!hasLogo) {
            log.warn("Aucun logo fourni pour le design | Un logo par défaut sera utilisé");
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    private String getValidStyles() {
        return String.join(", ", 
            java.util.Arrays.stream(WidgetAppearance.values())
                .map(Enum::name)
                .toArray(String[]::new)
        );
    }

    private String getValidJerseyDesigns() {
        return String.join(", ", 
            java.util.Arrays.stream(JerseyDesign.values())
                .map(Enum::name)
                .toArray(String[]::new)
        );
    }
}