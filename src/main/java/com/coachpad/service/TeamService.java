package com.coachpad.service;

import com.coachpad.dto.TeamDTO;
import java.util.List;
import java.util.Optional;

public interface TeamService {
    
    List<TeamDTO> getAllTeams();
    
    Optional<TeamDTO> getTeamById(Long id);
    
    Optional<TeamDTO> getTeamByName(String name);
    
    List<TeamDTO> searchTeamsByName(String name);
    
    List<TeamDTO> getTeamsByFormationId(Long formationId);
    
    Optional<TeamDTO> getTeamByHeadCoachId(Long coachId);
    
    long countTeams();
    
    boolean teamNameExists(String name);
    
    TeamDTO createTeam(TeamDTO teamDTO);
    
    TeamDTO updateTeam(Long id, TeamDTO teamDTO);
    
    void deleteTeam(Long id);
    
    TeamDTO removeDesignFromTeam(Long teamId);

    void cleanupExcelTeams();
}