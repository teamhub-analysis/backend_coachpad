package com.coachpad.service;

import com.coachpad.persistence.entity.TeamKitColorsEntity;
import java.util.List;
import java.util.Optional;

public interface TeamKitColorsService {

    TeamKitColorsEntity createColors(TeamKitColorsEntity colors);

    TeamKitColorsEntity updateColors(Long id, TeamKitColorsEntity colors);

    Optional<TeamKitColorsEntity> getColorsById(Long id);

    List<TeamKitColorsEntity> getAllColors();

    void deleteColors(Long id);
}
