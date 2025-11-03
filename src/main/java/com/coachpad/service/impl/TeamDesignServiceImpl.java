package com.coachpad.service.impl;

import com.coachpad.dto.TeamDesignDTO;  // ← Changé de Model à DTO
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.mapper.TeamKitColorsMapper;
import com.coachpad.persistence.entity.TeamDesignEntity;
import com.coachpad.persistence.entity.TeamKitColorsEntity;
import com.coachpad.persistence.repository.TeamDesignRepository;
import com.coachpad.service.TeamDesignService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamDesignServiceImpl implements TeamDesignService {

    private final TeamDesignRepository teamDesignRepository;
    private final TeamDesignMapper teamDesignMapper;
    private final TeamKitColorsMapper colorsMapper;

    @Override
    public List<TeamDesignDTO> getAllDesigns() {
        return teamDesignRepository.findAllWithColors()
                .stream()
                .map(teamDesignMapper::toDTO)  // ← toDTO au lieu de toModel
                .collect(Collectors.toList());
    }

    @Override
    public Optional<TeamDesignDTO> getDesignById(Long id) {
        return teamDesignRepository.findByIdWithColors(id)
                .map(teamDesignMapper::toDTO);  // ← toDTO
    }

    @Override
    public Optional<TeamDesignDTO> getDesignByTeamId(Long teamId) {
        return teamDesignRepository.findByTeamId(teamId)
                .map(teamDesignMapper::toDTO);  // ← toDTO
    }

    @Override
    public List<TeamDesignDTO> getDesignsByStyle(String style) {
        return teamDesignRepository.findByStyle(style)
                .stream()
                .map(teamDesignMapper::toDTO)  // ← toDTO
                .collect(Collectors.toList());
    }

    @Override
    public List<TeamDesignDTO> getDesignsByJerseyDesign(String jerseyDesign) {
        return teamDesignRepository.findByJerseyDesign(jerseyDesign)
                .stream()
                .map(teamDesignMapper::toDTO)  // ← toDTO
                .collect(Collectors.toList());
    }

    @Override
    public TeamDesignDTO createDesign(TeamDesignDTO dto) {  // ← DTO au lieu de Model
        TeamDesignEntity entity = teamDesignMapper.toEntity(dto);

        // Lier correctement les couleurs si elles existent
        if (entity.getColors() != null) {
            entity.getColors().setDesign(entity);
        }

        TeamDesignEntity saved = teamDesignRepository.save(entity);
        return teamDesignMapper.toDTO(saved);  // ← toDTO
    }

    @Override
    public TeamDesignDTO updateDesign(Long id, TeamDesignDTO dto) {  // ← DTO
        TeamDesignEntity existing = teamDesignRepository.findByIdWithColors(id)
                .orElseThrow(() -> new RuntimeException("Design non trouvé avec id : " + id));

        // Mettre à jour le design avec MapStruct
        teamDesignMapper.updateEntityFromDTO(dto, existing);  // ← updateEntityFromDTO

        // Mettre à jour les couleurs correctement
        // Note: Vérifiez si TeamKitColorsDTO existe ou utilisez le bon type
        if (dto.getColors() != null) {
            if (existing.getColors() == null) {
                TeamKitColorsEntity newColors = colorsMapper.toEntity(dto.getColors());
                newColors.setDesign(existing);
                existing.setColors(newColors);
            } else {
                colorsMapper.updateEntityFromDto(dto.getColors(), existing.getColors());
            }
        }

        TeamDesignEntity updated = teamDesignRepository.save(existing);
        return teamDesignMapper.toDTO(updated);  // ← toDTO
    }

    @Override
    public void deleteDesign(Long id) {
        teamDesignRepository.deleteById(id);
    }
}