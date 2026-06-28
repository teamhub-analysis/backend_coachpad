package com.coachpad.domain.usecase;

import com.coachpad.presentation.rest.dto.*;
import com.coachpad.domain.model.enums.ProjectCategory;
import com.coachpad.infrastructure.persistance.postgresql.entity.ProjectEntity;
import com.coachpad.infrastructure.service.ProjectService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlanningUseCases {

    private final ProjectService projectService;

    // ===== HELPERS =====

    private MacrocycleDTO toMacrocycleDTO(ProjectEntity e) {
        return MacrocycleDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private MesocycleDTO toMesocycleDTO(ProjectEntity e) {
        return MesocycleDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .parentId(e.getParentId())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private MicrocycleDTO toMicrocycleDTO(ProjectEntity e) {
        return MicrocycleDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .parentId(e.getParentId())
                .startDate(e.getStartDate())
                .endDate(e.getEndDate())
                .weekType(e.getWeekType() != null ? e.getWeekType() : "PrÃƒÂ©paration")
                .microcycleNumber(e.getMicrocycleNumber() != null ? e.getMicrocycleNumber() : 1)
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private TrainingSessionDTO toSessionDTO(ProjectEntity e) {
        return TrainingSessionDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .parentId(e.getParentId())
                .date(e.getStartDate() != null ? e.getStartDate() : e.getCreatedAt())
                .timeSlot(e.getTimeSlot())
                .sessionNumber(e.getSessionNumber())
                .intensity(e.getIntensity())
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private DrillDTO toDrillDTO(ProjectEntity e) {
        return DrillDTO.builder()
                .id(e.getId())
                .name(e.getName())
                .description(e.getDescription())
                .parentId(e.getParentId())
                .rpe(e.getIntensity())
                .durationMinutes(e.getTotalDurationSeconds() / 60)
                .minPlayers(e.getHomePlayerCount() + e.getAwayPlayerCount())
                .maxPlayers(e.getPlayerCount())
                .objectif(e.getObjectif())
                .organisation(e.getOrganisation())
                .consignes(e.getConsignes())
                .variantes(e.getVariantes())
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private MatchEventDTO toMatchDTO(ProjectEntity e) {
        return MatchEventDTO.builder()
                .id(e.getId())
                .parentId(e.getParentId())
                .opponentName(e.getOpponentName() != null ? e.getOpponentName() : "Adversaire")
                .matchDate(e.getMatchDate() != null ? e.getMatchDate() : e.getCreatedAt())
                .homeTeamId(e.getHomeTeamId())
                .awayTeamId(e.getAwayTeamId())
                .homeTeamName(e.getHomeTeamName())
                .awayTeamName(e.getAwayTeamName())
                .createdAt(e.getCreatedAt())
                .lastModified(e.getLastModified())
                .isFavorite(e.isFavorite())
                .isArchived(e.isArchived())
                .build();
    }

    private ProjectEntity fromMacrocycleDTO(MacrocycleDTO dto) {
        ProjectEntity e = new ProjectEntity();
        e.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        e.setName(dto.getName());
        e.setDescription(dto.getDescription());
        e.setCategory(ProjectCategory.MACROCYCLE);
        e.setStartDate(dto.getStartDate());
        e.setEndDate(dto.getEndDate());
        e.setFavorite(dto.isFavorite());
        e.setArchived(dto.isArchived());
        return e;
    }

    // ===== MACROCYCLES =====

    public List<MacrocycleDTO> getAllMacrocycles(Long userId) {
        return projectService.getProjectsByCategoryForUser(ProjectCategory.MACROCYCLE, userId)
                .stream().map(this::toMacrocycleDTO).collect(Collectors.toList());
    }

    public Optional<MacrocycleDTO> getMacrocycleById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.MACROCYCLE)
                .map(this::toMacrocycleDTO);
    }

    @Transactional
    public MacrocycleDTO saveMacrocycle(MacrocycleDTO dto, Long userId) {
        ProjectEntity entity = fromMacrocycleDTO(dto);
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toMacrocycleDTO(saved);
    }

    @Transactional
    public void deleteMacrocycle(String id) {
        projectService.deleteProject(id);
    }

    // ===== MESOCYCLES =====

    public List<MesocycleDTO> getMesocyclesForMacrocycle(String macrocycleId, Long userId) {
        return projectService.getChildProjectsForUser(macrocycleId, ProjectCategory.MESOCYCLE, userId)
                .stream().map(this::toMesocycleDTO).collect(Collectors.toList());
    }

    public Optional<MesocycleDTO> getMesocycleById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.MESOCYCLE)
                .map(this::toMesocycleDTO);
    }

    @Transactional
    public MesocycleDTO saveMesocycle(MesocycleDTO dto, Long userId) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        entity.setCategory(ProjectCategory.MESOCYCLE);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setFavorite(dto.isFavorite());
        entity.setArchived(dto.isArchived());
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toMesocycleDTO(saved);
    }

    @Transactional
    public void deleteMesocycle(String id) {
        projectService.deleteProject(id);
    }

    // ===== MICROCYCLES =====

    public List<MicrocycleDTO> getMicrocyclesForMesocycle(String mesocycleId, Long userId) {
        return projectService.getChildProjectsForUser(mesocycleId, ProjectCategory.MICROCYCLE, userId)
                .stream().map(this::toMicrocycleDTO).collect(Collectors.toList());
    }

    public Optional<MicrocycleDTO> getMicrocycleById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.MICROCYCLE)
                .map(this::toMicrocycleDTO);
    }

    @Transactional
    public MicrocycleDTO saveMicrocycle(MicrocycleDTO dto, Long userId) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        entity.setCategory(ProjectCategory.MICROCYCLE);
        entity.setStartDate(dto.getStartDate());
        entity.setEndDate(dto.getEndDate());
        entity.setWeekType(dto.getWeekType());
        entity.setMicrocycleNumber(dto.getMicrocycleNumber());
        entity.setFavorite(dto.isFavorite());
        entity.setArchived(dto.isArchived());
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toMicrocycleDTO(saved);
    }

    @Transactional
    public void deleteMicrocycle(String id) {
        projectService.deleteProject(id);
    }

    // ===== SESSIONS =====

    public List<TrainingSessionDTO> getSessionsForMicrocycle(String microcycleId, Long userId) {
        return projectService.getChildProjectsForUser(microcycleId, ProjectCategory.SESSION, userId)
                .stream().map(this::toSessionDTO).collect(Collectors.toList());
    }

    public Optional<TrainingSessionDTO> getSessionById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.SESSION)
                .map(this::toSessionDTO);
    }

    @Transactional
    public TrainingSessionDTO saveSession(TrainingSessionDTO dto, Long userId) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        entity.setCategory(ProjectCategory.SESSION);
        entity.setStartDate(dto.getDate());
        entity.setEndDate(dto.getDate());
        entity.setTimeSlot(dto.getTimeSlot());
        entity.setSessionNumber(dto.getSessionNumber());
        entity.setIntensity(dto.getIntensity());
        entity.setFavorite(dto.isFavorite());
        entity.setArchived(dto.isArchived());
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toSessionDTO(saved);
    }

    @Transactional
    public void deleteSession(String id) {
        projectService.deleteProject(id);
    }

    // ===== DRILLS =====

    public List<DrillDTO> getDrillsForSession(String sessionId, Long userId) {
        return projectService.getChildProjectsForUser(sessionId, ProjectCategory.EXERCISE, userId)
                .stream().map(this::toDrillDTO).collect(Collectors.toList());
    }

    public Optional<DrillDTO> getDrillById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.EXERCISE)
                .map(this::toDrillDTO);
    }

    @Transactional
    public DrillDTO saveDrill(DrillDTO dto, Long userId) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setParentId(dto.getParentId());
        entity.setCategory(ProjectCategory.EXERCISE);
        entity.setIntensity(dto.getRpe());
        entity.setTotalDurationSeconds(dto.getDurationMinutes() * 60);
        entity.setHomePlayerCount(dto.getMinPlayers() / 2);
        entity.setAwayPlayerCount(dto.getMinPlayers() / 2);
        entity.setPlayerCount(dto.getMaxPlayers());
        entity.setObjectif(dto.getObjectif());
        entity.setOrganisation(dto.getOrganisation());
        entity.setConsignes(dto.getConsignes());
        entity.setVariantes(dto.getVariantes());
        entity.setFavorite(dto.isFavorite());
        entity.setArchived(dto.isArchived());
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toDrillDTO(saved);
    }

    @Transactional
    public void deleteDrill(String id) {
        projectService.deleteProject(id);
    }

    // ===== MATCHES =====

    public List<MatchEventDTO> getMatchesForMicrocycle(String microcycleId, Long userId) {
        return projectService.getChildProjectsForUser(microcycleId, ProjectCategory.MATCH, userId)
                .stream().map(this::toMatchDTO).collect(Collectors.toList());
    }

    public Optional<MatchEventDTO> getMatchById(String id, Long userId) {
        return projectService.getProjectByIdForUser(id, userId)
                .filter(p -> p.getCategory() == ProjectCategory.MATCH)
                .map(this::toMatchDTO);
    }

    @Transactional
    public MatchEventDTO saveMatch(MatchEventDTO dto, Long userId) {
        ProjectEntity entity = new ProjectEntity();
        entity.setId(dto.getId() != null ? dto.getId() : java.util.UUID.randomUUID().toString());
        entity.setName(dto.getOpponentName());
        entity.setParentId(dto.getParentId());
        entity.setCategory(ProjectCategory.MATCH);
        entity.setMatchDate(dto.getMatchDate());
        entity.setOpponentName(dto.getOpponentName());
        entity.setHomeTeamId(dto.getHomeTeamId());
        entity.setAwayTeamId(dto.getAwayTeamId());
        entity.setHomeTeamName(dto.getHomeTeamName());
        entity.setAwayTeamName(dto.getAwayTeamName());
        entity.setFavorite(dto.isFavorite());
        entity.setArchived(dto.isArchived());
        ProjectEntity saved = projectService.saveProjectForUser(entity, userId);
        return toMatchDTO(saved);
    }

    @Transactional
    public void deleteMatch(String id) {
        projectService.deleteProject(id);
    }

    // ===== GENERATION =====

    @Transactional
    public List<MicrocycleDTO> generateMicrocycles(
            String mesocycleId, Long userId, String weekType) {
        Optional<ProjectEntity> parentOpt = projectService.getProjectByIdForUser(mesocycleId, userId);
        if (parentOpt.isEmpty()) return List.of();

        ProjectEntity parent = parentOpt.get();
        LocalDateTime start = parent.getStartDate();
        LocalDateTime end = parent.getEndDate();
        if (start == null || end == null) return List.of();

        List<MicrocycleDTO> result = new ArrayList<>();
        LocalDateTime current = start;
        int weekNum = 1;

        while (current.isBefore(end)) {
            LocalDateTime weekEnd = current.plusDays(6);
            MicrocycleDTO dto = MicrocycleDTO.builder()
                    .name("Semaine " + weekNum)
                    .parentId(mesocycleId)
                    .startDate(current)
                    .endDate(weekEnd)
                    .weekType(weekType != null ? weekType : "PrÃƒÂ©paration")
                    .microcycleNumber(weekNum)
                    .build();
            result.add(saveMicrocycle(dto, userId));
            current = current.plusDays(7);
            weekNum++;
        }

        return result;
    }
}
