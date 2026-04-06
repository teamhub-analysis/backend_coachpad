// src/main/java/com/coachpad/persistence/adapter/TeamAdapter.java
package com.coachpad.persistence.adapter;

import com.coachpad.dto.*;
import com.coachpad.mapper.CoachMapper;
import com.coachpad.mapper.PlayerMapper;
import com.coachpad.mapper.TeamDesignMapper;
import com.coachpad.mapper.TeamMapper;
import com.coachpad.mapper.SquadGroupMapper;
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
    private final PlayerMapper playerMapper;
    private final SquadGroupMapper squadGroupMapper;
    private final CoachMapper coachMapper;


    // ==================== READ OPERATIONS ====================

    @Transactional(readOnly = true)
    public List<TeamDTO> findAll() {
        return teamMapper.toDTOList(teamRepository.findAllWithCoaches());
    }

    @Transactional(readOnly = true)
    public java.util.Optional<TeamDTO> findById(Long id) {
        return teamRepository.findWithAllRelationsById(id).map(entity -> {
            // S'assurer que les joueurs sont chargés si nécessaire par le mapper
            entity.getPlayers().size();
            return teamMapper.toDTO(entity);
        });
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
        if (dto.getHeadCoachId() != null) {
            CoachEntity headCoach = fetchCoach(dto.getHeadCoachId());
            headCoach.setRole(com.coachpad.persistence.Enum.CoachRole.HEAD_COACH);
            entity.addCoach(headCoach);
        }

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
                    .map(playerMapper::toEntity) // PlayerDTO → PlayerEntity
                    .peek(player -> player.setTeam(entity)) // LIAISON AUTOMATIQUE
                    .toList();
            entity.setPlayers(players);
        } else if (dto.getPlayerIds() != null && !dto.getPlayerIds().isEmpty()) {
            // → JOUEURS DÉJÀ EXISTANTS
            List<PlayerEntity> players = fetchPlayers(dto.getPlayerIds());
            entity.setPlayers(players);
        }

        // 5. SQUAD GROUPS
        if (dto.getGroups() != null && !dto.getGroups().isEmpty()) {
            List<SquadGroupEntity> groups = dto.getGroups().stream()
                    .map(groupDto -> {
                        SquadGroupEntity group = squadGroupMapper.toEntity(groupDto);
                        group.setTeam(entity);
                        if (groupDto.getPlayerIds() != null) {
                            group.setPlayers(fetchPlayers(groupDto.getPlayerIds()));
                        }
                        return group;
                    })
                    .toList();
            entity.setGroups(groups);
        }

        // 6. SAUVEGARDE EN CASCADE
        TeamEntity saved = teamRepository.save(entity);
        return teamMapper.toDTO(saved);
    }

    @Transactional
    public TeamDTO update(Long id, TeamDTO dto) {
        TeamEntity entity = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée : " + id));

        validateUniqueName(id, dto.getName());
        teamMapper.updateEntityFromDTO(dto, entity);

        if (dto.getFormationId() != null) {
            entity.setFormation(fetchFormation(dto.getFormationId()));
        }

        // --- GESTION DU STAFF (COACHES) ---
        if (dto.getCoaches() != null) {
            // Mise à jour de la liste des coaches si fournie
            entity.getCoaches().clear();
            for (CoachDTO coachDto : dto.getCoaches()) {
                CoachEntity coach;
                if (coachDto.getId() != null && coachDto.getId() > 0) {
                    coach = coachRepository.findById(coachDto.getId())
                            .orElseGet(() -> coachMapper.toEntity(coachDto));
                } else {
                    coach = coachMapper.toEntity(coachDto);
                }
                coach.setTeam(entity);
                entity.addCoach(coach);
            }
        } else if (dto.getHeadCoachId() != null) {
            // Logique fallback : gestion par ID d'entraîneur principal uniquement
            CoachEntity headCoach = fetchCoach(dto.getHeadCoachId());
            headCoach.setRole(com.coachpad.persistence.Enum.CoachRole.HEAD_COACH);
            entity.getCoaches().removeIf(c -> c.getRole() == com.coachpad.persistence.Enum.CoachRole.HEAD_COACH
                    && !c.getId().equals(dto.getHeadCoachId()));
            if (entity.getCoaches().stream().noneMatch(c -> c.getId().equals(dto.getHeadCoachId()))) {
                entity.addCoach(headCoach);
            }
        }

        // --- GESTION DU STAFF MÉDICAL ---
        if (dto.getMedicalStaff() != null) {
            entity.getMedicalStaff().clear();
            for (CoachDTO staffDto : dto.getMedicalStaff()) {
                CoachEntity staff;
                if (staffDto.getId() != null && staffDto.getId() > 0) {
                    staff = coachRepository.findById(staffDto.getId())
                            .orElseGet(() -> coachMapper.toEntity(staffDto));
                } else {
                    staff = coachMapper.toEntity(staffDto);
                }
                staff.setTeam(entity);
                entity.addMedicalStaff(staff);
            }
        }


        // DESIGN
        if (dto.getDesign() != null) {
            if (entity.getDesign() == null) {
                TeamDesignEntity design = designMapper.toEntity(dto.getDesign());
                design.setTeam(entity);
                entity.setDesign(design);
            } else {
                designMapper.updateEntityFromDTO(dto.getDesign(), entity.getDesign());
            }
        }

        // JOUEURS (Conditionnel!)
        // On ne vide la liste que si de nouveaux joueurs ou IDs sont fournis
        if (dto.getPlayers() != null || dto.getPlayerIds() != null) {
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
        }

        // SQUAD GROUPS (Conditionnel!)
        if (dto.getGroups() != null) {
            entity.getGroups().clear();
            List<SquadGroupEntity> groups = dto.getGroups().stream()
                    .map(groupDto -> {
                        SquadGroupEntity group = squadGroupMapper.toEntity(groupDto);
                        group.setTeam(entity);
                        if (groupDto.getPlayerIds() != null) {
                            group.setPlayers(fetchPlayers(groupDto.getPlayerIds()));
                        }
                        return group;
                    })
                    .toList();
            entity.getGroups().addAll(groups);
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

    /**
     * Supprime toutes les équipes qui ne sont pas des équipes "core" (Real Madrid, PSG)
     */
    @Transactional
    public void cleanupExcelTeams() {
        List<String> coreTeams = java.util.Arrays.asList(
            "Real Madrid", 
            "Real Madrid Castilla", 
            "Real Madrid Juvenil A", 
            "Paris Saint-Germain", 
            "PSG U19", 
            "PSG U17"
        );
        
        List<TeamEntity> allTeams = teamRepository.findAll();
        List<TeamEntity> teamsToDelete = allTeams.stream()
            .filter(t -> !coreTeams.contains(t.getName()))
            .toList();
            
        teamRepository.deleteAll(teamsToDelete);
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
        boolean exists = (excludeId == null)
                ? teamRepository.existsByName(name)
                : teamRepository.existsByNameAndIdNot(name, excludeId);

        if (exists) {
            // ✅ IllegalStateException → vrai 409
            throw new IllegalStateException("Nom d'équipe déjà utilisé : " + name);
        }
    }

    private FormationEntity fetchFormation(Long id) {
        if (id == null)
            return null;
        return formationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Formation non trouvée : " + id));
    }

    private CoachEntity fetchCoach(Long id) {
        if (id == null)
            return null;
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