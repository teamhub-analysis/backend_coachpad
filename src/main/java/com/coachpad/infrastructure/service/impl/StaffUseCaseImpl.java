package com.coachpad.infrastructure.service.impl;

import com.coachpad.domain.model.CoachModel;
import com.coachpad.domain.repository.StaffRepository;
import com.coachpad.domain.usecase.StaffUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StaffUseCaseImpl implements StaffUseCase {

    private final StaffRepository staffRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CoachModel> getStaffByTeamId(Long teamId) {
        return staffRepository.getStaffByTeamId(teamId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CoachModel> getStaffById(Long id) {
        return staffRepository.getStaffById(id);
    }

    @Override
    public CoachModel createStaff(Long teamId, CoachModel coach) {
        return staffRepository.createStaff(teamId, coach);
    }

    @Override
    public CoachModel updateStaff(Long id, CoachModel coach) {
        return staffRepository.updateStaff(id, coach);
    }

    @Override
    public void deleteStaff(Long id) {
        staffRepository.deleteStaff(id);
    }

    @Override
    public CoachModel updateStaffPhoto(Long id, String photoUrl) {
        return staffRepository.updateStaffPhoto(id, photoUrl);
    }
}
