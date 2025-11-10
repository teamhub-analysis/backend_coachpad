package com.coachpad.service;

import com.coachpad.dto.FormationDTO;
import java.util.List;

public interface FormationService {
    List<FormationDTO> createFormation(List<FormationDTO> dtos);
    FormationDTO updateFormation(Long id, FormationDTO dto);
    FormationDTO getFormationById(Long id);
    void deleteFormation(Long id);
    List<FormationDTO> getAllFormations();
    List<FormationDTO> getValidFormations();
    List<FormationDTO> getMostUsedFormations();
    void deleteUnusedFormations();
    long countTeamsUsingFormation(Long formationId);
}