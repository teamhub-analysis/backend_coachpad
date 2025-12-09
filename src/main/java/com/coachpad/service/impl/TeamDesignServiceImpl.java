package com.coachpad.service.impl;

import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.dto.TeamKitColorsDTO;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.mapper.TeamKitColorsMapper;
import com.coachpad.persistence.Enum.JerseyDesign;
import com.coachpad.persistence.Enum.WidgetAppearance;
import com.coachpad.persistence.entity.TeamDesignEntity;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
import com.coachpad.persistence.repository.TeamDesignRepository;
import com.coachpad.service.TeamDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TeamDesignServiceImpl implements TeamDesignService {

    private final TeamDesignRepository teamDesignRepository;
    private final TeamDesignMapper teamDesignMapper;
    private final TeamKitColorsMapper colorsMapper;

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignDTO> getAllDesigns() {
        return teamDesignRepository.findAllWithColors()
                .stream()
                .map(teamDesignMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignDTO> getDesignById(Long id) {
        return teamDesignRepository.findByIdWithColors(id)
                .map(teamDesignMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TeamDesignDTO> getDesignByTeamId(Long teamId) {
        return teamDesignRepository.findByTeamId(teamId)
                .map(teamDesignMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignDTO> getDesignsByStyle(String style) {
        try {
            // Convertir String en Enum (insensible à la casse)
            WidgetAppearance designStyle = WidgetAppearance.valueOf(style.toUpperCase());
            return teamDesignRepository.findByStyle(designStyle)
                    .stream()
                    .map(teamDesignMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Style invalide : " + style + 
                ". Valeurs acceptées : " + String.join(", ", getValidStyles()));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeamDesignDTO> getDesignsByJerseyDesign(String jerseyDesign) {
        try {
            // Convertir String en Enum (insensible à la casse)
            JerseyDesign jersey = JerseyDesign.valueOf(jerseyDesign.toUpperCase());
            return teamDesignRepository.findByJerseyDesign(jersey)
                    .stream()
                    .map(teamDesignMapper::toDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Jersey design invalide : " + jerseyDesign + 
                ". Valeurs acceptées : " + String.join(", ", getValidJerseyDesigns()));
        }
    }

    @Override
    public TeamDesignDTO createDesign(TeamDesignDTO dto) {
        // Validation : vérifier si l'équipe a déjà un design
        if (dto.getId() != null && teamDesignRepository.existsByTeamId(dto.getId())) {
            throw new IllegalArgumentException("Cette équipe possède déjà un design");
        }

        TeamDesignEntity entity = teamDesignMapper.toEntity(dto);

        // Lier correctement les couleurs si elles existent
        if (entity.getColors() != null) {
            entity.getColors().setDesign(entity);
        }

        TeamDesignEntity saved = teamDesignRepository.save(entity);
        return teamDesignMapper.toDTO(saved);
    }

    @Override
    public TeamDesignDTO updateDesign(Long id, TeamDesignDTO dto) {
        TeamDesignEntity existing = teamDesignRepository.findByIdWithColors(id)
                .orElseThrow(() -> new RuntimeException("Design non trouvé avec id : " + id));

        // Mettre à jour le design avec MapStruct
        teamDesignMapper.updateEntityFromDTO(dto, existing);

        // Mettre à jour les couleurs correctement
        TeamKitColorsDTO colorsDTO = dto.getColors();
        if (colorsDTO != null) {
            if (existing.getColors() == null) {
                TeamKitColorsEntity newColors = colorsMapper.toEntity(colorsDTO);
                newColors.setDesign(existing);
                existing.setColors(newColors);
            } else {
                colorsMapper.updateEntityFromDto(colorsDTO, existing.getColors());
            }
        } else {
            // Si pas de couleurs dans le DTO, supprimer les couleurs existantes
            existing.setColors(null);
        }

        TeamDesignEntity updated = teamDesignRepository.save(existing);
        return teamDesignMapper.toDTO(updated);
    }

    @Override
    public void deleteDesign(Long id) {
        if (!teamDesignRepository.existsById(id)) {
            throw new RuntimeException("Design non trouvé avec id : " + id);
        }
        teamDesignRepository.deleteById(id);
    }

    // === MÉTHODES UTILITAIRES PRIVÉES ===

    private List<String> getValidStyles() {
        return java.util.Arrays.stream(WidgetAppearance.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }

    private List<String> getValidJerseyDesigns() {
        return java.util.Arrays.stream(JerseyDesign.values())
                .map(Enum::name)
                .collect(Collectors.toList());
    }
}