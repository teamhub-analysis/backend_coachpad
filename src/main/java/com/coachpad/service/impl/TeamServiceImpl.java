package com.coachpad.service.impl;

import com.coachpad.dto.TeamDTO;
import com.coachpad.persistence.adapter.TeamAdapter;
import com.coachpad.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamAdapter teamAdapter;

    @Override
    public List<TeamDTO> getAllTeams() {
        return teamAdapter.findAll();
    }

    @Override
    public Optional<TeamDTO> getTeamById(Long id) {
        return teamAdapter.findById(id);
    }

    @Override
    public Optional<TeamDTO> getTeamByName(String name) {
        return teamAdapter.findByName(name);
    }

    @Override
    public List<TeamDTO> searchTeamsByName(String name) {
        return teamAdapter.searchByName(name);
    }

    @Override
    public List<TeamDTO> getTeamsByFormationId(Long formationId) {
        return teamAdapter.findByFormationId(formationId);
    }

    @Override
    public Optional<TeamDTO> getTeamByHeadCoachId(Long coachId) {
        return teamAdapter.findByHeadCoachId(coachId);
    }

    @Override
    public long countTeams() {
        return teamAdapter.count();
    }

    @Override
    public boolean teamNameExists(String name) {
        return teamAdapter.existsByName(name);
    }

    @Override
    public TeamDTO createTeam(TeamDTO teamDTO) {
        return teamAdapter.create(teamDTO);
    }

    @Override
    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        return teamAdapter.update(id, teamDTO);
    }

    @Override
    public TeamDTO removeDesignFromTeam(Long teamId) {
        return teamAdapter.removeDesign(teamId);
    }
}
