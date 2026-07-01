package com.coachpad.domain.usecase;

import com.coachpad.domain.model.CoachModel;

import java.util.List;
import java.util.Optional;

public interface StaffUseCase {
    List<CoachModel> getStaffByTeamId(Long teamId);
    Optional<CoachModel> getStaffById(Long id);
    CoachModel createStaff(Long teamId, CoachModel coach);
    CoachModel updateStaff(Long id, CoachModel coach);
    void deleteStaff(Long id);
    CoachModel updateStaffPhoto(Long id, String photoUrl);
}
