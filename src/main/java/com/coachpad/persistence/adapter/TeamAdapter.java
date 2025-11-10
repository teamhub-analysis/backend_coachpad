// src/main/java/com/coachpad/persistence/adapter/TeamAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.dto.*;
import com.coachpad.mapper.PlayerMapper;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.mapper.TeamMapper;
import com.coachpad.persistence.entity.*;
import com.coachpad.persistence.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamAdapter {

    private final TeamRepository teamRepository;
    private final FormationRepository formationRepository;
    private final CoachRepository coachRepository;
    private final PlayerRepository playerRepository;
    private final TeamMapper teamMapper;
    private final TeamDesignMapper designMapper;
    private final PlayerMapper playerMapper; // INJECTÉ

    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public List<TeamDTO> findAll() {
        return teamMapper.toDTOList(teamRepository.findAll());
    }

    @Transactional(readOnly = true)
    public java.util.Optional<TeamDTO> findById(Long id) {
        return teamRepository.findById(id).map(teamMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public java.util.Optional<TeamDTO> findByName(String name) {
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
    public java.util.Optional<TeamDTO> findByHeadCoachId(Long coachId) {
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

    /**
     * CRÉE UNE ÉQUIPE COMPLÈTE EN 1 REQUÊTE
     * - Équipe + Design + 11 joueurs (créés)
     * - OU Équipe + playerIds (joueurs existants)
     */
    @Transactional
    public TeamDTO create(TeamDTO dto) {
        validateUniqueName(null, dto.getName());

        // 1. ÉQUIPE
        TeamEntity entity = teamMapper.toEntity(dto);
        entity.setFormation(fetchFormation(dto.getFormationId()));
        entity.setHeadCoach(fetchCoach(dto.getHeadCoachId()));

        // 2. DESIGN
        if (dto.getDesign() != null) {
            TeamDesignEntity design = designMapper.toEntity(dto.getDesign());
            design.setTeam(entity);
            entity.setDesign(design);
        }

        // 3. JOUEURS
        if (dto.getPlayers() != null && !dto.getPlayers().isEmpty()) {
            // → CRÉATION COMPLÈTE DES JOUEURS
            List<PlayerEntity> players = dto.getPlayers().stream()
                .map(playerMapper::toEntity)           // PlayerDTO → PlayerEntity
                .peek(player -> player.setTeam(entity)) // LIAISON AUTOMATIQUE
                .toList();
            entity.setPlayers(players);
        }
        else if (dto.getPlayerIds() != null && !dto.getPlayerIds().isEmpty()) {
            // → JOUEURS DÉJÀ EXISTANTS
            List<PlayerEntity> players = fetchPlayers(dto.getPlayerIds());
            entity.setPlayers(players);
        }

        // 4. SAUVEGARDE EN CASCADE
        TeamEntity saved = teamRepository.save(entity);
        return teamMapper.toDTO(saved);
    }

    @Transactional
    public TeamDTO update(Long id, TeamDTO dto) {
        TeamEntity entity = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée : " + id));

        validateUniqueName(id, dto.getName());
        teamMapper.updateEntityFromDTO(dto, entity);

        entity.setFormation(fetchFormation(dto.getFormationId()));
        entity.setHeadCoach(fetchCoach(dto.getHeadCoachId()));

        // DESIGN
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

        // JOUEURS
        entity.getPlayers().clear();
        if (dto.getPlayers() != null && !dto.getPlayers().isEmpty()) {
            List<PlayerEntity> players = dto.getPlayers().stream()
                .map(playerMapper::toEntity)
                .peek(p -> p.setTeam(entity))
                .toList();
            entity.getPlayers().addAll(players);
        } else if (dto.getPlayerIds() != null && !dto.getPlayerIds().isEmpty()) {
            List<PlayerEntity> players = fetchPlayers(dto.getPlayerIds());
            entity.getPlayers().addAll(players);
        }

        return teamMapper.toDTO(teamRepository.save(entity));
    }

    @Transactional
    public void delete(Long id) {
        if (!teamRepository.existsById(id)) {
            throw new EntityNotFoundException("Équipe non trouvée : " + id);
        }
        teamRepository.deleteById(id);
    }

    @Transactional
    public TeamDTO addDesign(Long teamId, TeamDesignDTO designDTO) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée : " + teamId));

        TeamDesignEntity design = designMapper.toEntity(designDTO);
        design.setTeam(team);
        team.setDesign(design);

        return teamMapper.toDTO(teamRepository.save(team));
    }

    @Transactional
    public TeamDTO removeDesign(Long teamId) {
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée : " + teamId));

        team.setDesign(null);
        return teamMapper.toDTO(teamRepository.save(team));
    }

    // ==================== PRIVATE UTILS ====================

    private void validateUniqueName(Long excludeId, String name) {
        if (teamRepository.existsByNameAndIdNot(name, excludeId)) {
            throw new IllegalArgumentException("Nom d'équipe déjà utilisé : " + name);
        }
    }

    private FormationEntity fetchFormation(Long id) {
        if (id == null) return null;
        return formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée : " + id));
    }

    private CoachEntity fetchCoach(Long id) {
        if (id == null) return null;
        return coachRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Coach non trouvé : " + id));
    }

    private List<PlayerEntity> fetchPlayers(List<Long> ids) {
        List<PlayerEntity> players = playerRepository.findAllById(ids);
        if (players.size() != ids.size()) {
            List<Long> missing = ids.stream()
                    .filter(id -> players.stream().noneMatch(p -> p.getId().equals(id)))
                    .toList();
            throw new EntityNotFoundException("Joueurs non trouvés : " + missing);
        }
        return players;
    }
}