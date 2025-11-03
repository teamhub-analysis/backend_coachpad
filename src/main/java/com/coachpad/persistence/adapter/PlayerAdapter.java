package com.coachpad.persistence.adapter;

import com.coachpad.dto.PlayerDTO;
import com.coachpad.mapper.PlayerMapper;
import com.coachpad.persistence.entity.PlayerEntity;
import com.coachpad.persistence.entity.TeamEntity;
import com.coachpad.persistence.repository.PlayerRepository;
import com.coachpad.persistence.repository.TeamRepository;
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

    /**
     * Trouve tous les joueurs
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findAll() {
        return playerMapper.toDTOList(playerRepository.findAll());
    }

    /**
     * Trouve un joueur par ID
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findById(Long id) {
        return playerRepository.findById(id)
                .map(playerMapper::toDTO);
    }

    /**
     * Trouve un joueur par email
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findByEmail(String email) {
        return playerRepository.findByEmail(email)
                .map(playerMapper::toDTO);
    }

    /**
     * Trouve tous les joueurs d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findByTeamIdOrderByNumberAsc(teamId)
        );
    }

    /**
     * Trouve un joueur par numéro dans une équipe
     */
    @Transactional(readOnly = true)
    public Optional<PlayerDTO> findByNumberAndTeamId(Integer number, Long teamId) {
        return playerRepository.findByNumberAndTeamId(number, teamId)
                .map(playerMapper::toDTO);
    }

    /**
     * Trouve tous les joueurs actifs d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findActivePlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findActivePlayersByTeamId(teamId)
        );
    }

    /**
     * Trouve tous les joueurs par position
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findByMainPosition(String position) {
        return playerMapper.toDTOList(
                playerRepository.findByMainPosition(position)
        );
    }

    /**
     * Trouve tous les joueurs d'une équipe par position
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findByTeamIdAndPosition(Long teamId, String position) {
        return playerMapper.toDTOList(
                playerRepository.findByTeamIdAndMainPosition(teamId, position)
        );
    }

    /**
     * Recherche des joueurs par nom
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> searchByName(String name) {
        return playerMapper.toDTOList(
                playerRepository.searchByName(name)
        );
    }

    /**
     * Trouve les meilleurs buteurs d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findTopScorersByTeamId(Long teamId, int limit) {
        List<PlayerEntity> scorers = playerRepository.findTopScorersByTeamId(teamId);
        return playerMapper.toDTOList(
                scorers.stream().limit(limit).toList()
        );
    }

    /**
     * Trouve les joueurs avec le plus de passes décisives
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findTopAssistersByTeamId(Long teamId, int limit) {
        List<PlayerEntity> assisters = playerRepository.findTopAssistersByTeamId(teamId);
        return playerMapper.toDTOList(
                assisters.stream().limit(limit).toList()
        );
    }

    /**
     * Trouve les joueurs blessés d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findInjuredPlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findInjuredPlayersByTeamId(teamId)
        );
    }

    /**
     * Trouve les joueurs suspendus d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findSuspendedPlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findSuspendedPlayersByTeamId(teamId)
        );
    }

    /**
     * Trouve les joueurs disponibles d'une équipe
     */
    @Transactional(readOnly = true)
    public List<PlayerDTO> findAvailablePlayersByTeamId(Long teamId) {
        return playerMapper.toDTOList(
                playerRepository.findAvailablePlayersByTeamId(teamId)
        );
    }

    /**
     * Calcule la moyenne des notes d'une équipe
     */
    @Transactional(readOnly = true)
    public Double calculateAverageRatingByTeamId(Long teamId) {
        return playerRepository.calculateAverageRatingByTeamId(teamId);
    }

    /**
     * Compte le nombre de joueurs dans une équipe
     */
    @Transactional(readOnly = true)
    public long countByTeamId(Long teamId) {
        return playerRepository.countByTeamId(teamId);
    }

    /**
     * Vérifie si un email existe
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return playerRepository.existsByEmail(email);
    }

    /**
     * Vérifie si un numéro existe dans une équipe
     */
    @Transactional(readOnly = true)
    public boolean existsByNumberAndTeamId(Integer number, Long teamId) {
        return playerRepository.existsByNumberAndTeamId(number, teamId);
    }

    /**
     * Crée un nouveau joueur
     */
    @Transactional
    public PlayerDTO create(PlayerDTO playerDTO) {
        // Vérifier que l'équipe existe
        TeamEntity team = teamRepository.findById(playerDTO.getTeamId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "L'équipe avec l'ID " + playerDTO.getTeamId() + " n'existe pas"
                ));

        // Vérifier que le numéro n'est pas déjà utilisé
        if (existsByNumberAndTeamId(playerDTO.getNumber(), playerDTO.getTeamId())) {
            throw new IllegalArgumentException(
                    "Le numéro " + playerDTO.getNumber() + " est déjà utilisé dans cette équipe"
            );
        }

        // Vérifier que l'email n'est pas déjà utilisé
        if (playerDTO.getEmail() != null && existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "L'email " + playerDTO.getEmail() + " est déjà utilisé"
            );
        }

        PlayerEntity entity = playerMapper.toEntity(playerDTO);
        entity.setTeam(team);
        
        PlayerEntity savedEntity = playerRepository.save(entity);
        return playerMapper.toDTO(savedEntity);
    }

    /**
     * Met à jour un joueur existant
     */
    @Transactional
    public PlayerDTO update(Long id, PlayerDTO playerDTO) {
        PlayerEntity existingEntity = playerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Le joueur avec l'ID " + id + " n'existe pas"
                ));

        // Vérifier le changement de numéro
        if (!existingEntity.getNumber().equals(playerDTO.getNumber()) &&
            existsByNumberAndTeamId(playerDTO.getNumber(), playerDTO.getTeamId())) {
            throw new IllegalArgumentException(
                    "Le numéro " + playerDTO.getNumber() + " est déjà utilisé dans cette équipe"
            );
        }

        // Vérifier le changement d'email
        if (playerDTO.getEmail() != null && 
            !playerDTO.getEmail().equals(existingEntity.getEmail()) &&
            existsByEmail(playerDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "L'email " + playerDTO.getEmail() + " est déjà utilisé"
            );
        }

        // Vérifier le changement d'équipe
        if (!existingEntity.getTeam().getId().equals(playerDTO.getTeamId())) {
            TeamEntity newTeam = teamRepository.findById(playerDTO.getTeamId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "L'équipe avec l'ID " + playerDTO.getTeamId() + " n'existe pas"
                    ));
            existingEntity.setTeam(newTeam);
        }

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
                    "Le joueur avec l'ID " + id + " n'existe pas"
            );
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