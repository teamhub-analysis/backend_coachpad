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
     * CRÉE OU MET À JOUR UNE ÉQUIPE (UPSERT)
     * Si le nom d'équipe existe déjà, met à jour l'équipe existante.
     */
    @Transactional
    public TeamDTO create(TeamDTO dto) {
        // 1. GESTION DU NOM UNIQUE (Auto-renommage)
        String originalName = dto.getName();
        String currentName = originalName;
        int counter = 1;

        // Tant que le nom existe, on incrémente le suffixe (ex: "Nom (1)")
        while (teamRepository.existsByName(currentName)) {
            currentName = originalName + " (" + counter + ")";
            counter++;
        }
        
        if (!currentName.equals(originalName)) {
            System.out.println("🔄 Renommage automatique de l'équipe : " + originalName + " -> " + currentName);
            dto.setName(currentName);
        }

        // Sinon, création normale
        TeamEntity entity = teamMapper.toEntity(dto);
        // Forcer l'ID à null pour garantir une création (POST)
        entity.setId(null);
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
            // -> CRÉATION COMPLÈTE DES JOUEURS
            List<PlayerEntity> players = dto.getPlayers().stream()
                    .map(playerMapper::toEntity) // PlayerDTO -> PlayerEntity
                    .peek(player -> player.setTeam(entity)) // LIAISON AUTOMATIQUE
                    .toList();
            entity.setPlayers(players);
        } else if (dto.getPlayerIds() != null && !dto.getPlayerIds().isEmpty()) {
            // -> JOUEURS DÉJÀ EXISTANTS
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

        // PROTECTION : Si c'est une équipe protégée et qu'on tente de changer son nom
        // On préfère créer une nouvelle équipe plutôt que d'écraser l'officielle
        List<String> coreTeams = java.util.Arrays.asList("France", "France Espoirs", "France U19", "Tunisia", "Tunisia U23", "Tunisia U20");
        if (coreTeams.contains(entity.getName()) && !entity.getName().equals(dto.getName())) {
            // On réinitialise l'ID pour forcer une création d'une nouvelle entité
            dto.setId(null);
            return create(dto);
        }

        validateUniqueName(id, dto.getName());
        teamMapper.updateEntityFromDTO(dto, entity);

        if (dto.getFormationId() != null) {
            entity.setFormation(fetchFormation(dto.getFormationId()));
        }

        // --- GESTION DU STAFF (COACHES) ---
        if (dto.getCoaches() != null) {
            final List<CoachEntity> currentCoaches = entity.getCoaches();
            final List<CoachDTO> newCoachDTOs = dto.getCoaches();

            // 1. Supprimer ceux qui ne sont plus là
            currentCoaches.removeIf(c -> 
                newCoachDTOs.stream().noneMatch(dtoC -> dtoC.getId() != null && dtoC.getId().equals(c.getId()))
            );

            // 2. Mettre à jour ou ajouter
            for (CoachDTO cDto : newCoachDTOs) {
                if (cDto.getId() != null && cDto.getId() > 0) {
                    currentCoaches.stream()
                        .filter(c -> c.getId().equals(cDto.getId()))
                        .findFirst()
                        .ifPresent(c -> coachMapper.updateEntityFromDTO(cDto, c));
                } else {
                    CoachEntity newC = coachMapper.toEntity(cDto);
                    newC.setTeam(entity);
                    currentCoaches.add(newC);
                }
            }
        }

        // --- GESTION DU STAFF MÉDICAL ---
        if (dto.getMedicalStaff() != null) {
            final List<CoachEntity> currentMedical = entity.getMedicalStaff();
            final List<CoachDTO> newMedicalDTOs = dto.getMedicalStaff();

            currentMedical.removeIf(m -> 
                newMedicalDTOs.stream().noneMatch(dtoM -> dtoM.getId() != null && dtoM.getId().equals(m.getId()))
            );

            for (CoachDTO mDto : newMedicalDTOs) {
                if (mDto.getId() != null && mDto.getId() > 0) {
                    currentMedical.stream()
                        .filter(m -> m.getId().equals(mDto.getId()))
                        .findFirst()
                        .ifPresent(m -> coachMapper.updateEntityFromDTO(mDto, m));
                } else {
                    CoachEntity newM = coachMapper.toEntity(mDto);
                    newM.setTeam(entity);
                    currentMedical.add(newM);
                }
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

        // JOUEURS (Merge Strategy)
        if (dto.getPlayers() != null) {
            final List<PlayerEntity> currentPlayers = entity.getPlayers();
            final List<PlayerDTO> newPlayerDTOs = dto.getPlayers();

            // 1. Marquer les joueurs à supprimer (ceux qui sont en DB mais pas dans le DTO)
            currentPlayers.removeIf(p -> {
                boolean stay = newPlayerDTOs.stream().anyMatch(dtoP -> dtoP.getId() != null && dtoP.getId().equals(p.getId()));
                return !stay;
            });

            // 2. Mettre à jour les existants ou ajouter les nouveaux
            for (PlayerDTO pDto : newPlayerDTOs) {
                if (pDto.getId() != null && pDto.getId() > 0) {
                    // Existant
                    currentPlayers.stream()
                        .filter(p -> p.getId().equals(pDto.getId()))
                        .findFirst()
                        .ifPresent(p -> {
                        playerMapper.updateEntityFromDTO(pDto, p);
                        p.setTeam(entity);
                    });
                } else {
                    // Nouveau
                    PlayerEntity newP = playerMapper.toEntity(pDto);
                    newP.setTeam(entity);
                    currentPlayers.add(newP);
                }
            }
        } else if (dto.getPlayerIds() != null && !dto.getPlayerIds().isEmpty()) {
            List<PlayerEntity> players = fetchPlayers(dto.getPlayerIds());
            entity.getPlayers().clear();
            entity.getPlayers().addAll(players);
        }

        // SQUAD GROUPS (Merge Strategy)
        if (dto.getGroups() != null) {
            final List<SquadGroupEntity> currentGroups = entity.getGroups();
            final List<SquadGroupDTO> newGroupDTOs = dto.getGroups();

            currentGroups.removeIf(g -> {
                boolean stay = newGroupDTOs.stream().anyMatch(dtoG -> dtoG.getId() != null && dtoG.getId().equals(g.getId()));
                return !stay;
            });

            for (SquadGroupDTO gDto : newGroupDTOs) {
                if (gDto.getId() != null && gDto.getId() > 0) {
                    // Existant
                    currentGroups.stream()
                        .filter(g -> g.getId().equals(gDto.getId()))
                        .findFirst()
                        .ifPresent(g -> {
                            squadGroupMapper.updateEntityFromDTO(gDto, g);
                            g.setTeam(entity);
                            if (gDto.getPlayerIds() != null) {
                                g.setPlayers(fetchPlayers(gDto.getPlayerIds()));
                            }
                        });
                } else {
                    // Nouveau
                    SquadGroupEntity newG = squadGroupMapper.toEntity(gDto);
                    newG.setTeam(entity);
                    if (gDto.getPlayerIds() != null) {
                        newG.setPlayers(fetchPlayers(gDto.getPlayerIds()));
                    }
                    currentGroups.add(newG);
                }
            }
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
     * Supprime toutes les équipes qui ne sont pas des équipes "core" (France, Tunisia)
     */
    @Transactional
    public void cleanupExcelTeams() {
        List<String> coreTeams = java.util.Arrays.asList(
            "France", 
            "France Espoirs", 
            "France U19", 
            "Tunisia", 
            "Tunisia U23", 
            "Tunisia U20",
            "Brésil",
            "Allemagne"
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
            // ✅ IllegalStateException -> vrai 409
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
