// src/main/java/com/coachpad/persistence/adapter/TeamAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.dto.TeamDTO;
import com.coachpad.dto.TeamDesignDTO;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.mapper.TeamMapper;
import com.coachpad.persistence.entity.*;
import com.coachpad.persistence.repository.*;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TeamAdapter {

    private final TeamRepository teamRepository;
    private final FormationRepository formationRepository;
    private final CoachRepository coachRepository;
    private final TeamDesignRepository teamDesignRepository;
    private final TeamMapper teamMapper;
    private final TeamDesignMapper designMapper;

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public List<TeamDTO> findAll() {
        return teamMapper.toDTOList(teamRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<TeamDTO> findById(Long id) {
        return teamRepository.findById(id).map(teamMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<TeamDTO> findByName(String name) {
        return teamRepository.findByName(name).map(teamMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> searchByName(String name) {
        return teamMapper.toDTOList(teamRepository.searchByName(name));
    }

    @Transactional(readOnly = true)
    public List<TeamDTO> findByFormationId(Long formationId) {
        return teamMapper.toDTOList(teamRepository.findByFormationId(formationId));
    }

    @Transactional(readOnly = true)
    public Optional<TeamDTO> findByHeadCoachId(Long coachId) {
        return teamRepository.findByHeadCoachId(coachId).map(teamMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public long count() {
        return teamRepository.count();
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return teamRepository.existsByName(name);
    }

    // ==================== WRITE OPERATIONS ====================

    @Transactional
    public TeamDTO create(TeamDTO dto) {
        validateUniqueName(null, dto.getName());

        TeamEntity entity = teamMapper.toEntity(dto);
        entity.setFormation(fetchFormation(dto.getFormationId()));
        entity.setHeadCoach(fetchCoach(dto.getHeadCoachId()));

        if (dto.getDesign() != null) {
            TeamDesignEntity design = designMapper.toEntity(dto.getDesign());
            design.setTeam(entity);
            entity.setDesign(design);
        }

        return teamMapper.toDTO(teamRepository.save(entity));
    }

    @Transactional
    public TeamDTO update(Long id, TeamDTO dto) {
        TeamEntity entity = teamRepository.findById(id)
                .orElseThrow();

        validateUniqueName(id, dto.getName());
        teamMapper.updateEntityFromDTO(dto, entity);

        entity.setFormation(fetchFormation(dto.getFormationId()));
        entity.setHeadCoach(fetchCoach(dto.getHeadCoachId()));

        if (dto.getDesign() != null) {
            if (entity.getDesign() == null) {
                TeamDesignEntity design = designMapper.toEntity(dto.getDesign());
                design.setTeam(entity);
                entity.setDesign(design);
            } else {
                designMapper.updateEntityFromDTO(dto.getDesign(), entity.getDesign());
            }
        } else {
            entity.setDesign(null);
        }

        return teamMapper.toDTO(teamRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) throws Exception {
        if (!teamRepository.existsById(id)) {
            throw new Exception("Team not found: " + id);
        }
        teamRepository.deleteById(id);
    }

    @Transactional
    public TeamDTO addDesign(Long teamId, TeamDesignDTO designDTO) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow();

        TeamDesignEntity design = designMapper.toEntity(designDTO);
        design.setTeam(team);
        team.setDesign(design);

        return teamMapper.toDTO(teamRepository.save(team));
    }

    @Transactional
    public TeamDTO removeDesign(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Team not found: " + teamId));

        team.setDesign(null);
        return teamMapper.toDTO(teamRepository.save(team));
    }

    // ==================== PRIVATE UTILS ====================

    private void validateUniqueName(Long excludeId, String name) {
        if (teamRepository.existsByNameAndIdNot(name, excludeId)) {
            throw new IllegalArgumentException("Team name already exists: " + name);
        }
    }

    private FormationEntity fetchFormation(Long id) {
        return id != null
                ? formationRepository.findById(id)
                        .orElseThrow()
                : null;
    }

    private CoachEntity fetchCoach(Long id) {
        return id != null
                ? coachRepository.findById(id)
                        .orElseThrow()
                : null;
    }
}