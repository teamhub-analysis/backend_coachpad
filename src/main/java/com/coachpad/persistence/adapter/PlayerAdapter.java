package com.coachpad.persistence.adapter;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.mapper.PlayerMapper;
import com.coachpad.persistence.entity.PlayerEntity;
import com.coachpad.persistence.entity.TeamEntity;
import com.coachpad.persistence.repository.PlayerRepository;
import com.coachpad.persistence.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException; // Import ajouté pour les nouvelles méthodes
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PlayerAdapter {

    private final PlayerRepository playerRepository;
    private final TeamRepository teamRepository;
    private final PlayerMapper playerMapper;

    // =================================================================
    // MÉTHODES DE LECTURE (GET)
    // =================================================================

    @Transactional(readOnly = true)
    public List<PlayerDTO> findAll() {
        return playerMapper.toDTOList(playerRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findById(Long id) {
        return playerRepository.findById(id)
                .map(playerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findByEmail(String email) {
        return playerRepository.findByEmail(email)
                .map(playerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findByTeamIdOrderByNumberAsc(teamId));
    }

    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findByNumberAndTeamId(Integer number, Long teamId) {
        return playerRepository.findByNumberAndTeamId(number, teamId)
                .map(playerMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findActivePlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findActivePlayersByTeamId(teamId));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findByMainPosition(String position) {
        return playerMapper.toDTOList(
                playerRepository.findByMainPosition(position));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findByTeamIdAndPosition(Long teamId, String position) {
        return playerMapper.toDTOList(
                playerRepository.findByTeamIdAndMainPosition(teamId, position));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> searchByName(String name) {
        return playerMapper.toDTOList(
                playerRepository.searchByName(name));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findTopScorersByTeamId(Long teamId, int limit) {
        List<PlayerEntity> scorers = playerRepository.findTopScorersByTeamId(teamId);
        return playerMapper.toDTOList(
                scorers.stream().limit(limit).toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findTopAssistersByTeamId(Long teamId, int limit) {
        List<PlayerEntity> assisters = playerRepository.findTopAssistersByTeamId(teamId);
        return playerMapper.toDTOList(
                assisters.stream().limit(limit).toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findInjuredPlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findInjuredPlayersByTeamId(teamId));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findSuspendedPlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findSuspendedPlayersByTeamId(teamId));
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> findAvailablePlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findAvailablePlayersByTeamId(teamId));
    }

    @Transactional(readOnly = true)
    public Double calculateAverageRatingByTeamId(Long teamId) {
        return playerRepository.calculateAverageRatingByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public long countByTeamId(Long teamId) {
        return playerRepository.countByTeamId(teamId);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByNumberAndTeamId(Integer number, Long teamId) {
        return playerRepository.existsByNumberAndTeamId(number, teamId);
    }

    // =================================================================
    // MÉTHODES D'ÉCRITURE (POST, PUT, DELETE)
    // =================================================================

    /**
     * Crée un nouveau joueur (attend un teamId dans le DTO)
     */
    @Transactional
    public PlayerDTO create(PlayerDTO playerDTO) {
        TeamEntity team = teamRepository.findById(playerDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "L'équipe avec l'ID " + playerDTO.getId() + " n'existe pas"));

        if (existsByNumberAndTeamId(playerDTO.getNumber(), playerDTO.getId())) {
            throw new IllegalArgumentException(
                    "Le numéro " + playerDTO.getNumber() + " est déjà utilisé dans cette équipe");
        }

        if (playerDTO.getEmail() != null && existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "L'email " + playerDTO.getEmail() + " est déjà utilisé");
        }

        PlayerEntity entity = playerMapper.toEntity(playerDTO);
        entity.setTeam(team);
        PlayerEntity savedEntity = playerRepository.save(entity);
        return playerMapper.toDTO(savedEntity);
    }

    // --- NOUVELLES MÉTHODES POUR AJOUTER DES JOUEURS À UNE ÉQUIPE ---

    /**
     * // NOUVEAU //
     * Crée un seul joueur et l'assigne à une équipe (teamId est dans l'URL)
     */
    @Transactional
    public PlayerDTO createForTeam(Long teamId, PlayerDTO playerDTO) {
        // 1. Vérifier que l'équipe existe
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec l'ID : " + teamId));

        // 2. Vérifier que le numéro n'est pas déjà utilisé dans CETTE équipe
        if (existsByNumberAndTeamId(playerDTO.getNumber(), teamId)) {
            throw new IllegalArgumentException(
                    "Le numéro " + playerDTO.getNumber() + " est déjà utilisé dans cette équipe.");
        }

        // 3. Vérifier que l'email n'est pas déjà utilisé (globalement)
        if (playerDTO.getEmail() != null && existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "L'email " + playerDTO.getEmail() + " est déjà utilisé par un autre joueur.");
        }

        // 4. Convertir le DTO en entité
        PlayerEntity playerToCreate = playerMapper.toEntity(playerDTO);

        // 5. Lier le joueur à l'équipe trouvée à l'étape 1
        playerToCreate.setTeam(team);

        // 6. Sauvegarder et retourner
        PlayerEntity savedPlayer = playerRepository.save(playerToCreate);
        return playerMapper.toDTO(savedPlayer);
    }

    /**
     * // NOUVEAU //
     * Crée une liste de joueurs et les assigne à une équipe (teamId est dans l'URL)
     */
    @Transactional
    public List<PlayerDTO> createBulkForTeam(Long teamId, List<PlayerDTO> playerDTOs) {
        // 1. Vérifier que l'équipe existe
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(() -> new EntityNotFoundException("Équipe non trouvée avec l'ID : " + teamId));

        // 2. Convertir la liste de DTOs en liste d'entités et lier chaque joueur à
        // l'équipe
        List<PlayerEntity> playerEntities = playerDTOs.stream()
                .map(playerMapper::toEntity)
                .peek(player -> player.setTeam(team))
                .toList();

        // 3. Sauvegarder tout en une seule fois (très performant)
        List<PlayerEntity> savedPlayers = playerRepository.saveAll(playerEntities);

        // 4. Retourner la liste des DTOs créés
        return savedPlayers.stream()
                .map(playerMapper::toDTO)
                .toList();
    }

    /**
     * Met à jour un joueur existant
     */
    @Transactional
    public PlayerDTO update(Long id, PlayerDTO playerDTO) {
        PlayerEntity existingEntity = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le joueur avec l'ID " + id + " n'existe pas"));

        // ✅ Utiliser l'ID de l'équipe existante, pas celui du joueur
        Long teamId = existingEntity.getTeam().getId();

        if (!existingEntity.getNumber().equals(playerDTO.getNumber()) &&
                existsByNumberAndTeamId(playerDTO.getNumber(), teamId)) { // ✅ teamId
            throw new IllegalArgumentException(
                    "Le numéro " + playerDTO.getNumber() + " est déjà utilisé dans cette équipe");
        }

        if (playerDTO.getEmail() != null &&
                !playerDTO.getEmail().equals(existingEntity.getEmail()) &&
                existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "L'email " + playerDTO.getEmail() + " est déjà utilisé");
        }

        // ✅ Supprimer le bloc qui changeait l'équipe via playerDTO.getId()
        // (un update de joueur ne doit pas changer son équipe)

        playerMapper.updateEntityFromDTO(playerDTO, existingEntity);
        PlayerEntity updatedEntity = playerRepository.save(existingEntity);
        return playerMapper.toDTO(updatedEntity);
    }

    /**
     * Supprime un joueur
     */
    @Transactional
    public void delete(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new IllegalArgumentException(
                    "Le joueur avec l'ID " + id + " n'existe pas");
        }
        playerRepository.deleteById(id);
    }

    /**
     * Supprime tous les joueurs d'une équipe
     */
    @Transactional
    public void deleteByTeamId(Long teamId) {
        playerRepository.deleteByTeamId(teamId);
    }
}