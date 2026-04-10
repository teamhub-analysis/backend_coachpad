package com.coachpad.service.impl;

import com.coachpad.dto.TeamDTO;
import com.coachpad.persistence.adapter.TeamAdapter;
import com.coachpad.service.TeamService;
import com.coachpad.service.ExcelImportService;
import com.coachpad.service.CsvImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamAdapter teamAdapter;
    private final ExcelImportService excelImportService;
    private final CsvImportService csvImportService;

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
    public void deleteTeam(Long id) {
        teamAdapter.delete(id);
    }

    @Override
    public TeamDTO removeDesignFromTeam(Long teamId) {
        return teamAdapter.removeDesign(teamId);
    }

    @Override
    public void cleanupExcelTeams() {
        teamAdapter.cleanupExcelTeams();
    }

    @Override
    public TeamDTO importTeamDirect(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        TeamDTO importedTeam;

        // ✅ 1. Lecture du fichier selon l'extension
        if (fileName != null && fileName.endsWith(".csv")) {
            importedTeam = csvImportService.importFullTeam(file);
        } else {
            importedTeam = excelImportService.importFullTeam(file);
        }

        // ✅ 2. Sauvegarde immédiate
        // La logique de renommage est déjà gérée dans teamAdapter.create
        return teamAdapter.create(importedTeam);
    }
}