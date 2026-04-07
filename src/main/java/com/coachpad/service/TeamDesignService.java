package com.coachpad.service;

import com.coachpad.dto.TeamDesignDTO;  // ← DTO au lieu de Model

import java.util.List;
import java.util.Optional;

public interface TeamDesignService {
    List<TeamDesignDTO> getAllDesigns();
    Optional<TeamDesignDTO> getDesignById(Long id);
    Optional<TeamDesignDTO> getDesignByTeamId(Long teamId);
    List<TeamDesignDTO> getDesignsByStyle(String style);
    List<TeamDesignDTO> getDesignsByJerseyDesign(String jerseyDesign);
    TeamDesignDTO createDesign(TeamDesignDTO dto);
    TeamDesignDTO updateDesign(Long id, TeamDesignDTO dto);
    TeamDesignDTO updateTeamLogo(Long teamId, org.springframework.web.multipart.MultipartFile file);
    void deleteDesign(Long id);
}